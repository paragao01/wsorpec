# 🛠️ Troubleshooting - Webservices SPC

## 📖 Sumário
- [Problemas Comuns](#problemas-comuns)
- [Logs Importantes](#logs-importantes)
- [Pontos de Atenção](#pontos-de-atenção)
- [Links Úteis](#links-úteis)

---

## 1. ❗ Problemas Comuns

### 🚫 Serviço não aparece na lista do Axis2
- Verifique o `services.xml` e a presença das classes compiladas.
- Reinicie o Tomcat após alterações.

### ❓ Erro 404 ou 500 ao acessar endpoint
- Confirme a URL.
- Verifique logs do Tomcat e do sistema.

### 🔒 Falha de autenticação
- Confirme `codigo` e `senha` enviados.
- Valide junto ao SPC se as credenciais estão ativas.

### ⚠️ Resposta SOAP com `<STATUS-RESPOSTA>` diferente de zero
- Verifique `<MENSAGEM-RESPOSTA>` para detalhes do erro.

### 🔑 Axis2 Admin não acessível
- Verifique mapeamento em `web.xml` e credenciais.

### 🕒 Timeout ou lentidão
- Monitore recursos do servidor e consulte logs.

---

## 2. 📄 Logs Importantes
- Tomcat: `logs/catalina.out`, `logs/localhost.*.log`
- Sistema: `logwsorpec/`
- Axis2: conforme configuração

---

## 3. ⚠️ Pontos de Atenção
- Sempre reinicie o Tomcat após alterações.
- Faça backup dos arquivos de configuração.
- Restrinja acesso à administração do Axis2.
- Monitore espaço em disco dos logs.

---

## 4. 🔗 Links Úteis
- [Documentação Técnica](./TECHNICAL_DOCUMENTATION.md)
- [Referência de API](./API_REFERENCE.md)
- [Guia de Deploy](./DEPLOYMENT_GUIDE.md) 