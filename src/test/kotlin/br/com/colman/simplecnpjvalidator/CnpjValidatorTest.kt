/*
 * Copyright 2024 Leonardo Colman Lopes
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

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forNone
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll


class CnpjValidatorTest : ShouldSpec({

    should("return true on valid CNPJs") {
        ValidCnpjGenerator.forAll { cnpj -> cnpj.isCnpj() }
    }

    should("return false on random strings") {
        Arb.string().forAll { cnpj -> !cnpj.isCnpj() }
    }

    should("return false on invalid CNPJs") {
        knownInvalidCnpjs.forNone { it.shouldBeCnpj() }
    }

    should("sanitize the String given replaceable characters and still return true on valid CNPJs") {
        ValidCnpjGenerator.map { "/..--$it..--." }.forAll { cnpj -> cnpj.isCnpj(listOf('.', '-', '/')) }
    }

    should("not sanitize unspecified characters") {
        ValidCnpjGenerator.map { "$it++" }.forAll { cnpj -> !cnpj.isCnpj(listOf('.', '-')) }
    }

    should("return true on valid Long typed CNPJ input") {
        60542797000180.isCnpj().shouldBeTrue()
    }


    should("return false on invalid length of Long typed CNPJ input") {
        999L.isCnpj().shouldBeFalse()
    }
})

private fun String.shouldBeCnpj() = this.isCnpj().shouldBeTrue()


// Generated some valid CNPJs and changed the verification digits
private val knownInvalidCnpjs = listOf(
    "11357093000123",
    "68151486000101",
    "05471381000101",
    "07745726000101",
    "09380554000137",
    "73363858000195",
    "44333835000140",
    "46238357000104",
    "82757825000111",
    "16932477000245",
    "78824450012196",
    "50517263000141",
    "13748669000109",
    "21636423000173",
    "28222543000171",
    "86026889000199"
)
