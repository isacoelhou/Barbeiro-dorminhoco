# Barbeiro-dorminhoco 
Em ciência da computação, o problema do barbeiro dorminhoco é um problema clássico
de comunicação inter-threads e sincronização entre múltiplas threads.
O problema é análogo a manter o barbeiro ocupado enquanto há clientes, e descansando
quando não há nenhum (fazendo isso de uma maneira ordenada).
O barbeiro e seus clientes correspondem as threads mencionadas acima.
O problema: Na barbearia há um barbeiro, uma cadeira de barbeiro e n cadeiras para
eventuais clientes esperarem a vez. Quando não há clientes, o barbeiro senta-se na
cadeira de barbeiro e cai no sono. Quando chega um cliente, ele precisa acordar o
barbeiro. Se outros clientes chegarem enquanto o barbeiro estiver cortando o cabelo de
um cliente, eles se sentarão (se houver cadeiras vazias) ou sairão da barbearia (se todas
as cadeiras estiverem ocupadas).
O problema é programar o barbeiro e os clientes sem cair em condições de disputa
(condições de corrida) (race conditions). Condições de disputa são situações em que
duas ou mais threads (ou processos) estão trabalhando juntas e podem compartilhar
algum armazenamento comum que cada uma pode ler e gravar. O resultado final
depende de quem executa precisamente quando. Os resultados da maioria dos
programas são bons, mas, de vez em quando, acontece algo estranho e inconsistente.
Esse problema é semelhante a situações com várias filas, como uma mesa de
atendimento de telemarketing com diversos atendentes e com um sistema
computadorizado de chamadas em espera, atendendo a um número limitado de
chamadas que chegam.

Descrição extraída de: Barbeiro Dorminhoco. [s.l.: s.n., s.d.]. Disponível em: <https://www.ic.unicamp.br/~islene/mc514/barbeiro/barbeiro.pdf>.

‌