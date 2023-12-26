# Simple CNPJ Validator

![Build](https://github.com/LeoColman/SimpleCnpjValidator/workflows/Build/badge.svg)
[![GitHub](https://img.shields.io/github/license/LeoColman/SimpleCnpjValidator.svg)](https://github.com/LeoColman/SimpleCnpjValidator/blob/master/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/br.com.colman.simplecnpjvalidator/simple-cnpj-validator.svg)](https://search.maven.org/search?q=g:br.com.colman.simplecnpjvalidator)
[![Zero Dependencies Badge](https://img.shields.io/badge/Dependencies-0-brightgreen)](build.gradle.kts)


O conceito de validação de CNPJ existe desde a criação do próprio documento. No entanto, observa-se que a validação deste documento é replicada em várias aplicações, em classes idênticas, copiadas e coladas.

Com o objetivo de simplificar esse tipo de validação (seja em casos de teste ou em cenários de verificação de cadastro), a biblioteca **Simple CNPJ Validator** traz essa funcionalidade de uma vez, evitando assim boilerplate e possibilidade de erros no reuso de classe.


# Utilizando
Para utilizar é bem simples. Primeiro importe no seu Gradle:

`implementation("br.com.colman.simplecnpjvalidator:simple-cnpj-validator:{version}")`

E utilize a função em qualquer String de seu código:

`"12.345.678/9012-34".isCnpj()`

Por padrão, os caracteres `.`, `-` e `/` são retirados da String (permitindo o formato `12.345.678/9012-34`, por exemplo), mas isso pode ser modificado através do parâmetro `charactersToIgnore`:

`"12.345.678/9012-34".isCnpj(charactersToIgnore = listOf('.', '/'))`

## Contribuindo

Sinta-se livre para abrir um pull request ou uma issue para contribuir com este projeto.
