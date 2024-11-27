# desafio-ubots

Nesse repositório eu implementei a atividade pedida no processo seletivo da Ubots, no qual foi pedido a criação de um programa que distribuisse os chamados de atendimento ao cliente para as equipes corretas, seguindo as políticas da empresa.
Também foi pedido que o programa, caso necessário, tivesse uma interface REST para suas operações.

A implementação foi feita utilizando o framework Spring Boot. A parte da implementação da interface REST e seus métodos está no arquivo `AtendimentoController.java` e a implementação da lógica de distribuição de chamados está dividida entre os serviços `AtendimentoService` e `Equipe Service`.

A minha ideia para a implementação foi utilizar, para cada uma das três equipes, uma tabela com um contador para cada um dos atendentes integrantes da equipe. 
Esses contadores são incrementados ao atribuir um atendimento ao atendente e decrementados ao finalizar um dos atendimentos que estava atribuido ao funcionário.
Sempre que um atendimento é adicionado à fila ou finalizado é acionada a função que atribui os atendimentos a funcionários que não tenham o máximo de atendimentos atribuidos.

No restante do projeto, foram adicionadas algumas classes para facilitar a manutenção desses dados, por meio de repositórios, dtos e os modelos de dados.
