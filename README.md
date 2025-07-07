# Sistema de Webservices SPC

## Visão Geral
Sistema legado em Java 8 para consultas a produtos/serviços do SPC (Serviço de Proteção ao Crédito), exposto via webservices SOAP utilizando Apache Axis2 e rodando em Apache Tomcat.

## Objetivos
- Realizar consultas a produtos do SPC
- Integrar sistemas financeiros e de crédito
- Fornecer respostas padronizadas via webservice

## Tecnologias Principais
- Java 8
- Apache Tomcat
- Apache Axis2

## Estrutura do Projeto
```
wsorpec/
  ├── src/br/wsorpec/servico/         # Serviços expostos (endpoints)
  ├── src/br/wsorpec/wscdlrio/        # Integrações e módulos de negócio
  ├── WebContent/WEB-INF/             # Configurações do Tomcat/Axis2
  └── ...
```

## Quick Start
1. Instale Java 8 e Apache Tomcat
2. Faça o deploy do projeto no Tomcat
3. Acesse os serviços via `/services/` (Axis2)

## Documentação Completa
- [Documentação Técnica](./TECHNICAL_DOCUMENTATION.md): arquitetura, configuração, manutenção, diagramas
- [Referência de API](./API_REFERENCE.md): endpoints, exemplos SOAP/XML/JSON
- [Guia de Deploy](./DEPLOYMENT_GUIDE.md) *(opcional, se existir)*
- [Troubleshooting](./TROUBLESHOOTING.md) *(opcional, se existir)*

## Estrutura de Entrega
- Todos os arquivos de documentação estão na raiz do projeto ou em `docs/`.
- Recomenda-se versionar a documentação junto ao código-fonte.

---
Para dúvidas técnicas, consulte a [Documentação Técnica](./TECHNICAL_DOCUMENTATION.md) ou a [Referência de API](./API_REFERENCE.md). 