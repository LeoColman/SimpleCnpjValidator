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

import kotlin.math.abs

/**
 * Verifies that this String is a CNPJ
 *
 * This function checks if a given string is a CNPJ (Cadastro Nacional da Pessoa Jurídica in Portuguese), which is an
 * identification number issued to Brazilian companies.
 *
 * All strings will first be sanitized, with [charactersToIgnore] chars removed from it (it's usual for the document
 * to come in the form of xx.xxx.xxx/xxxx-yy, which isn't a valid cnpj number), and then they'll be validated according 
 * to the CNPJ specification.
 *
 *
 * @see [https://pt.wikipedia.org/wiki/Cadastro_Nacional_da_Pessoa_Jur%C3%ADdica#Algoritmo_de_Valida%C3%A7%C3%A3o[carece_de_fontes?]
 * @see [http://normas.receita.fazenda.gov.br/sijut2consulta/link.action?visao=anotado&idAto=1893]
 */
fun String.isCnpj(charactersToIgnore: List<Char> = listOf('.', '-', '/')): Boolean {
    val cleanCnpj = this.filterNot { it in charactersToIgnore }
    if (cleanCnpj.containsInvalidCNPJChars() || cleanCnpj.isInvalidCNPJSize()) return false
    return cleanCnpj.hasValidVerificationDigits()
}

/**
 * Verifies that this Long is a CNPJ
 *
 * This function checks if a given string is a CNPJ (Cadastro Nacional da Pessoa Jurídica in Portuguese), which is an
 * identification number issued to Brazilian companies.
 *
 * All strings will first be sanitized, with [charactersToIgnore] chars removed from it (it's usual for the document
 * to come in the form of xx.xxx.xxx/xxxx-yy, which isn't a valid cnpj number), and then they'll be validated according
 * to the CNPJ specification.
 *
 *
 * @see [https://pt.wikipedia.org/wiki/Cadastro_Nacional_da_Pessoa_Jur%C3%ADdica#Algoritmo_de_Valida%C3%A7%C3%A3o[carece_de_fontes?]
 * @see [http://normas.receita.fazenda.gov.br/sijut2consulta/link.action?visao=anotado&idAto=1893]
 */
fun Long.isCnpj() : Boolean {
    val absNumber = abs(this)
    return absNumber.toString().isCnpj()
}

private fun String.containsInvalidCNPJChars() = this.any { !it.isDigit() }
private fun String.isInvalidCNPJSize() = this.length != 14

// Algorithm from https://www.geradorcnpj.com/algoritmo_do_cnpj.htm
private fun String.hasValidVerificationDigits(): Boolean {
    val firstTwelveDigits = substring(0..11)
    val digits = substring(12..13)

    return firstTwelveDigits.calculateDigits() == digits
}

private fun String.calculateDigits(): String {
    val numbers = map { it.toString().toInt() }
    val firstVerificationDigit = numbers.calculateFirstVerificationDigit()
    val secondDigit = numbers.calculateSecondVerificationDigit(firstVerificationDigit)

    return "$firstVerificationDigit$secondDigit"
}

private fun List<Int>.calculateFirstVerificationDigit(): Int {
    /* Given 12 CNPJ numbers, the first digit calculation works this way:

    There is a weight associated to each number index:
    CNPJ first twelve digits - | A | B | C | D | E | F | G | H | I | J | K | L |
    CNPJ index multiplier  -   | 5 | 4 | 3 | 2 | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 |

    We will then sum all the digits with their multiplier: A * 5 + B * 4 + C * 3 ...
    With that result, the first verifier digit will be calculated with the remainder of (SUM / 11)
    If the remainder is 0 or 1, the digit is ZERO. If it's >=2, the digit is (11 - remainder)
     */
    val firstTwelveDigits = this
    val weights = (5 downTo 2) + (9 downTo 2)
    val sum = firstTwelveDigits.withIndex().sumBy { (index, element) -> weights[index] * element }

    val remainder = sum % 11
    return if(remainder < 2) 0 else 11 - remainder
}

private fun List<Int>.calculateSecondVerificationDigit(firstVerificationDigit: Int): Int {
    /*
    In a similar way to calculating the first digit, the second digit also works with a table of weights and the numbers

    However, the first verification digit is added to the digits, and the multipliers change a bit

    CNPJ first twelve digits + first verification digit - | A | B | C | D | E | F | G | H | I | J | K | L | 1st v.d. |
    CNPJ Index multiplier                               - | 6 | 5 | 4 | 3 | 2 | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2        |

    And in the same fashion, the sum will be calculated as A * 6 + B * 5 + ... + 1st vd * 2
    The second verification digit uses the same formula:
    remainder = (SUM / 11)
    2nd digit = ZERO if remainder is 0 or 1, 11 - remainder otherwise
     */

    val firstThirteenDigits = this + firstVerificationDigit
    val weights = (6 downTo 2) + (9 downTo 2)
    val sum = firstThirteenDigits.withIndex().sumBy { (index, element) -> weights[index] * element }

    val remainder = sum % 11
    return if (remainder < 2) 0 else 11 - remainder
}
