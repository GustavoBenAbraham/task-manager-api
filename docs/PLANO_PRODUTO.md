# Plano do produto DinDin de Giro

## Direção

O DinDin de Giro será uma ferramenta clara para pessoas autônomas, MEIs e pequenos negócios organizarem o dinheiro. Também poderá ser usado para finanças pessoais, sempre em um espaço separado. A Conta Azul é uma referência de clareza e organização; o produto será desenvolvido em etapas menores, de forma que as regras sejam fáceis de entender.

Não é necessário ter CNPJ para criar um espaço de negócio. A solução deve atender atividades formais e informais.

## Modelo de uso acordado

```text
Pessoa cliente
  ├── Espaço pessoal: privado, não compartilhado com a gestora
  └── Um espaço do próprio negócio ou atividade
        ├── Proprietário: cliente, pode administrar os acessos
        └── Gestora financeira/contadora: acesso autorizado pelo cliente

Gestora financeira
  └── Pode trabalhar nos espaços de vários clientes, cada um com sua própria autorização

Administrador técnico da plataforma
  └── Papel separado, sem acesso padrão às finanças dos clientes
```

O cliente e a gestora financeira têm contas de usuário distintas e trabalham sobre os mesmos dados do negócio. Ambos podem consultar e editar as informações financeiras compartilhadas. O cliente pode remover a autorização da gestora. A gestora não recebe acesso ao espaço pessoal do cliente.

O administrador técnico cuida da plataforma e não é a contadora. Ser administrador técnico não concede acesso financeiro por padrão. Esse papel e qualquer fluxo excepcional de suporte ainda precisam ser implementados.

## Situação da primeira etapa

O código desta etapa implementa:

- Espaço pessoal criado no cadastro e um espaço próprio de negócio por cliente; o CNPJ é opcional.
- Seleção de espaços na interface e separação de contas, categorias, lançamentos e dashboard por espaço.
- Convite por e-mail para uma gestora, aceite por usuário autenticado com o mesmo e-mail e acesso compartilhado ao negócio.
- Revogação de acesso pelo proprietário.
- Registro do criador e da última pessoa que alterou cada conta, categoria ou lançamento.

Os convites são criados na aplicação e compartilhados manualmente por link, válido por sete dias. O envio automático de e-mail ainda não foi configurado. O registro de criador e última alteração não substitui um histórico imutável de auditoria.

As migrações `V8` e `V9` introduzem espaços e convites; `V10` cria as contas previstas. Antes de aplicar em produção, faça backup e revise registros antigos sem usuário: dados sem proprietário identificável permanecem sem espaço e não aparecem nas consultas por espaço. A publicação das migrações altera o esquema do banco.

## Como explicar o dinheiro

- **Previsto:** conta a pagar ou valor a receber que ainda não movimentou o dinheiro.
- **Realizado:** pagamento ou recebimento que já movimentou uma conta.
- **Saldo:** dinheiro registrado nas contas após as movimentações realizadas.

O módulo cobre valores previstos e movimentações realizadas. Uma conta prevista só altera o saldo e o resumo realizado depois do pagamento ou recebimento.

## Etapas

### 1. Espaços e acesso compartilhado — implementado localmente

Concluir a revisão de permissões, migrations, experiência de convite e documentação; depois validar em ambiente de publicação com backup. Proprietário e gestora devem ver o mesmo negócio, e nenhuma gestora deve conseguir selecionar ou consultar o espaço pessoal do cliente.

### 2. Contas a pagar e a receber — implementado localmente

Registrar valores previstos com descrição, valor, vencimento, categoria e situação. Contas pendentes podem ser editadas ou canceladas. Ao informar a data e a conta do pagamento ou recebimento, o sistema cria uma movimentação realizada vinculada ao título, sem permitir liquidação duplicada. Os valores previstos não entram no saldo até a liquidação.

### 3. Fluxo de caixa e saldos — implementado localmente

Mostrar saldo inicial, entradas e saídas realizadas, previsões pendentes e saldo projetado com um cálculo explicável. A tela inicial cobre o mês atual; a API aceita um intervalo de datas.

### 4. Visões e relatórios

Apresentar itens em aberto, vencidos e realizados, fluxo de caixa por período e filtros simples.

### 5. Papéis e administração técnica

Avaliar papéis adicionais para equipes e implementar a administração técnica da plataforma separada do acesso financeiro dos clientes.

### 6. Expansões

Avaliar relatórios gerenciais, recorrência, clientes e fornecedores, integração bancária, vendas, estoque e documentos fiscais somente depois de validar o núcleo financeiro.

## Princípios de trabalho

1. Desenvolver uma etapa pequena de ponta a ponta: dados, API e interface.
2. Explicar regras e termos em linguagem simples.
3. Conferir isolamento entre clientes e permissões antes de publicar.
4. Fazer backup antes de aplicar migrações em produção.
5. Atualizar este plano quando decisões do produto mudarem.
