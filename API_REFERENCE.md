# 📑 Referência de API - Webservices SPC

## 📖 Sumário
- [Endpoints Disponíveis](#endpoints-disponíveis)
- [1. Busca Endereço/Telefone CNPJ](#1-busca-endereçotelefone-cnpj)
- [2. Busca Endereço/Telefone CPF](#2-busca-endereçotelefone-cpf)
- [3. Empresarial Gold](#3-empresarial-gold)
- [4. Empresarial Top](#4-empresarial-top)
- [5. Negativação](#5-negativação)
- [6. Orpec Empresarial](#6-orpec-empresarial)
- [7. Orpec Pessoal](#7-orpec-pessoal)
- [8. Pessoal Gold](#8-pessoal-gold)
- [9. Pessoal Plus](#9-pessoal-plus)
- [10. SPCNet Estadual CNPJ](#10-spcnet-estadual-cnpj)
- [11. SPCNet Estadual CPF](#11-spcnet-estadual-cpf)
- [12. SPCNet Nacional CNPJ](#12-spcnet-nacional-cnpj)
- [13. SPCNet Nacional CPF](#13-spcnet-nacional-cpf)

---

## 🟢 Endpoints Disponíveis

| Serviço                        | Classe Responsável                        | Descrição                                      |
|--------------------------------|-------------------------------------------|------------------------------------------------|
| Busca Endereço/Telefone CNPJ   | ServicoBuscaEndTelefoneCnpj               | Consulta dados de endereço/telefone por CNPJ    |
| Busca Endereço/Telefone CPF    | ServicoBuscaEndTelefoneCpf                | Consulta dados de endereço/telefone por CPF     |
| Empresarial Gold               | ServicoEmpresarialGold                    | Consulta completa empresarial (Gold) por CNPJ   |
| Empresarial Top                | ServicoEmpresarialTop                     | Consulta empresarial simplificada por CNPJ      |
| Negativação                    | ServicoNegativacao                        | Inclusão, baixa e listagem de negativação      |
| Orpec Empresarial              | ServicoOrpecEmpresarial                   | Relatório empresarial detalhado (JSON)          |
| Orpec Pessoal                  | ServicoOrpecPessoal                       | Relatório pessoal detalhado (JSON)              |
| Pessoal Gold                   | ServicoPessoalGold                        | Consulta completa de pessoa física (Gold)       |
| Pessoal Plus                   | ServicoPessoalPlus                        | Consulta ampliada de pessoa física (Plus)       |
| SPCNet Estadual CNPJ           | ServicoSpcNetEstadualCnpj                 | Consulta estadual de CNPJ no SPCNet             |
| SPCNet Estadual CPF            | ServicoSpcNetEstadualCpf                  | Consulta estadual de CPF no SPCNet              |
| SPCNet Nacional CNPJ           | ServicoSpcNetNacionalCnpj                 | Consulta nacional de CNPJ no SPCNet             |
| SPCNet Nacional CPF            | ServicoSpcNetNacionalCpf                  | Consulta nacional de CPF no SPCNet              |

---

> ℹ️ **Dica:** Consulte exemplos de request/response SOAP/JSON em cada seção.

---

## 1. 🔎 Busca Endereço/Telefone CNPJ
- **Classe:** `ServicoBuscaEndTelefoneCnpj`
- **Métodos:**
  - `buscaEndTelefoneCnpj(String codigo, String senha, String cnpj)`
  - `respostaXML(String codigo, String senha, String cnpj, String ip)`
- **Descrição:** Consulta dados cadastrais e telefônicos de uma empresa pelo CNPJ.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cnpj` (String): CNPJ da empresa
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 2. 🔎 Busca Endereço/Telefone CPF
- **Classe:** `ServicoBuscaEndTelefoneCpf`
- **Métodos:**
  - `buscaEndTelefoneCpf(String codigo, String senha, String cpf)`
  - `respostaXML(String codigo, String senha, String cpf, String ip)`
- **Descrição:** Consulta dados cadastrais e telefônicos de uma pessoa física pelo CPF.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cpf` (String): CPF da pessoa
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 3. 🏢 Empresarial Gold
- **Classe:** `ServicoEmpresarialGold`
- **Métodos:**
  - `empresarialGold(String codigo, String senha, String cnpj, ...)`
  - `respostaXML(String codigo, String senha, String cnpj, ..., String ip)`
- **Descrição:** Consulta empresarial completa (Gold) para análise de crédito e risco de empresas.
- **Parâmetros:**
  - Diversos parâmetros relacionados a dados bancários, cheques, quadro societário, etc.
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 4. 🏢 Empresarial Top
- **Classe:** `ServicoEmpresarialTop`
- **Métodos:**
  - `empresarialTop(String codigo, String senha, String cnpj)`
  - `respostaXML(String codigo, String senha, String cnpj, String ip)`
- **Descrição:** Consulta empresarial simplificada para análise de crédito de empresas.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cnpj` (String): CNPJ da empresa
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 5. 🚫 Negativação
- **Classe:** `ServicoNegativacao`
- **Métodos:**
  - `incluir(String xml)`
  - `baixar(String xml)`
  - `listar(String xml)`
- **Descrição:** Serviços para inclusão, baixa e listagem de registros de negativação.
- **Parâmetros:**
  - `xml` (String): XML estruturado conforme modelo de inclusão/baixa/listagem
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 6. 📊 Orpec Empresarial
- **Classe:** `ServicoOrpecEmpresarial`
- **Métodos:**
  - `orpecEmpresarial(String codigo, String senha, String cnpj)`
  - `respostaJSON(String codigo, String senha, String cnpj, String ip)`
- **Descrição:** Consulta de relatório empresarial detalhado (JSON).
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cnpj` (String): CNPJ da empresa
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response JSON:**
```json
<!-- Exemplo a ser detalhado -->
```

---

## 7. 📊 Orpec Pessoal
- **Classe:** `ServicoOrpecPessoal`
- **Métodos:**
  - `orpecPessoal(String codigo, String senha, String cpf)`
  - `respostaJSON(String codigo, String senha, String cpf, String ip)`
- **Descrição:** Consulta de relatório pessoal detalhado (JSON).
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cpf` (String): CPF da pessoa
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response JSON:**
```json
<!-- Exemplo a ser detalhado -->
```

---

## 8. 👤 Pessoal Gold
- **Classe:** `ServicoPessoalGold`
- **Métodos:**
  - `pessoalGold(String codigo, String senha, String cpf)`
  - `respostaXML(String codigo, String senha, String cpf, String ip)`
- **Descrição:** Consulta completa de pessoa física (Gold) para análise de crédito.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cpf` (String): CPF da pessoa
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 9. 👤 Pessoal Plus
- **Classe:** `ServicoPessoalPlus`
- **Métodos:**
  - `pessoalPlus(String codigo, String senha, String cpf, String uf)`
  - `respostaXML(String codigo, String senha, String cpf, String uf, String ip)`
- **Descrição:** Consulta ampliada de pessoa física (Plus) para análise de crédito.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cpf` (String): CPF da pessoa
  - `uf` (String): UF da consulta
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 10. 🏢 SPCNet Estadual CNPJ
- **Classe:** `ServicoSpcNetEstadualCnpj`
- **Métodos:**
  - `spcnetEstCnpj(String codigo, String senha, String cnpj, String uf)`
  - `respostaXML(String codigo, String senha, String cnpj, String uf, String ip)`
- **Descrição:** Consulta estadual de CNPJ no SPCNet.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cnpj` (String): CNPJ da empresa
  - `uf` (String): UF da consulta
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 11. 👤 SPCNet Estadual CPF
- **Classe:** `ServicoSpcNetEstadualCpf`
- **Métodos:**
  - `spcnetEstCpf(String codigo, String senha, String cpf, String uf)`
  - `respostaXML(String codigo, String senha, String cpf, String uf, String ip)`
- **Descrição:** Consulta estadual de CPF no SPCNet.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cpf` (String): CPF da pessoa
  - `uf` (String): UF da consulta
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 12. 🏢 SPCNet Nacional CNPJ
- **Classe:** `ServicoSpcNetNacionalCnpj`
- **Métodos:**
  - `spcnetNacCnpj(String codigo, String senha, String cnpj)`
  - `respostaXML(String codigo, String senha, String cnpj, String ip)`
- **Descrição:** Consulta nacional de CNPJ no SPCNet.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cnpj` (String): CNPJ da empresa
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

## 13. 👤 SPCNet Nacional CPF
- **Classe:** `ServicoSpcNetNacionalCpf`
- **Métodos:**
  - `spcnetNacCpf(String codigo, String senha, String cpf)`
  - `respostaXML(String codigo, String senha, String cpf, String ip)`
- **Descrição:** Consulta nacional de CPF no SPCNet.
- **Parâmetros:**
  - `codigo` (String): Código do usuário
  - `senha` (String): Senha de acesso
  - `cpf` (String): CPF da pessoa
  - `ip` (String, opcional): IP do solicitante
- **Exemplo de Request SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```
- **Exemplo de Response SOAP:**
```xml
<!-- Exemplo a ser detalhado -->
```

---

> ℹ️ *Os demais serviços serão detalhados nas próximas seções.* 