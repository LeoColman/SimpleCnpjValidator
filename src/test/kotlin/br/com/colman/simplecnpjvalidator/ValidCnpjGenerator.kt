/*
 * Copyright 2019 Leonardo Colman Lopes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.colman.simplecnpjvalidator

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next


object ValidCnpjGenerator : Arb<String>() {

    override fun edgecases() = listOf(
        "00000000000191",
        "60542797000180",
        "94540565000105",
        "01626036000148",
        "53980815000140",
        "94572462000127",
        "15264719000107"
    )

    // https://pt.wikipedia.org/wiki/Cadastro_Nacional_da_Pessoa_Jur%C3%ADdica#Algoritmo_de_Valida%C3%A7%C3%A3o[carece_de_fontes?]
    // https://www.geradorcnpj.com/algoritmo_do_cnpj.htm
    override fun values(rs: RandomSource) = generateSequence {
        val digits = List(12) { randomDigit() }
        val firstVerifierDigit = digits.firstVerifierDigit()
        val secondVerifierDigit = digits.secondVerifierDigit(firstVerifierDigit)

        Sample(
            digits.joinToString(separator = "") + "$firstVerifierDigit" + "$secondVerifierDigit"
        )
    }


    private fun List<Int>.firstVerifierDigit(): Int {
        val weights = listOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        return calculateVerifierDigit(weights, this)
    }

    private fun List<Int>.secondVerifierDigit(firstVerifierDigit: Int): Int {
        val weights = listOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        return calculateVerifierDigit(weights, this + firstVerifierDigit)
    }

    private fun calculateVerifierDigit(weights: List<Int>, values: List<Int>): Int {
        var total = 0
        values.forEachIndexed { index, i ->
            total += i * weights[index]
        }

        val divisionRemainder = total % 11
        return if (divisionRemainder < 2) 0 else 11 - divisionRemainder
    }
}

private fun randomDigit() = Arb.int(0, 9).next()
