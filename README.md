
# Criaturas Saltitantes

  Sistema interativo com múltiplos usuários que inclui um minigame de simulação no qual criaturas saltitantes se movem pela tela, interagem entre si e realizam ações como roubo de ouro, formação de grupos (clusters) e absorção pelo guardião.
A simulação envolve regras de física simples (pulo e gravidade), movimentação horizontal e lógica de pontuação baseada nas interações entre as criaturas.

## Detalhes da Simulação
Física Simplificada: gravidade, pulo e movimento horizontal.

Interações Inteligentes:

Roubo de ouro entre bolas próximas.

Criação de clusters quando criaturas se aglomeram.

Um guardião que absorve clusters e protege, com colorização e comportamento únicos.

Geração de Pontos e Persistência: Pontos atribuídos ao usuário são registrados com persistência via SQLite.

Condições de Finalização: Simulação termina quando um guardião e apenas uma criatura permanecem, ou após um número fixo de interações (ticks).

## 🧠 Lógica da Simulação

- As **Criaturas** se movem horizontalmente e pulam usando um sistema de física com timers.
- Uma criatura pode **roubar parte do ouro** de uma vizinha próxima quando estiver autorizada a se mover.
- Quando duas ou mais criaturas se sobrepõem, **formam um cluster** somando seus valores.
- Um **Guardião** é adicionado no início da simulação e absorve clusters próximos.
- A simulação termina quando:
  - Restam apenas um guardião e uma criatura.
  - Um número máximo de interações é atingido (`maxInteration`).
  - O jogador atinge ou não o objetivo de pontos (500).

## 🧩 Componentes

- **Creature**: Representa uma entidade simples com movimento, ouro e possibilidade de roubo.
- **Guardian**: Um tipo especial de Creature que absorve clusters.
- **CreaturesPanel**: Painel gráfico principal que controla a renderização, timers e lógica de interação.
- **User**: Contador de pontos e histórico de simulações.
- **SQLite**: Interface para persistência de dados do usuário.

## 🎨 Visualização

- Criaturas: Azul
- Guardião: Verde
- Clusters: Roxo

## 🛠️ Tecnologias Usadas

- Java Swing (GUI)
- `javax.swing.Timer` para controle de tempo
- Orientação a Objetos
- Integração com banco de dados SQLite
- MVC simplificado
- Junit
- Mockito
- Jqwik
