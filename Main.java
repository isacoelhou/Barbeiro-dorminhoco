//----------------------------------PSEUDOALGORITMO--------------------------------------------
//Sala de espera com N cadeiras
//Clientes = 0 -> Barbeiro senta na cadeira e dorme
//Chega cliente -> barbeiro acorda
//Se chega cliente enquanto o barbeiro está ocupado -> cliente espera sentado(se tiver cadeiras)
//Se não tem cadeiras disponíveis para espera, o cliente vai embora
//Logo após  corte de cabelo o cliente vai embora

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.Random;

class BarberShop {
    // Semáforos
    static Semaphore customers = new Semaphore(0); // fila de clientes, 0 estado de espera para o proximo cliente, 1 barbeiro está pronto para começar a cortar
    static Semaphore barbers = new Semaphore(0); // diz se o barbeiro está disponível ou não. 0 livre, 1 ocupado
    static Semaphore mutex = new Semaphore(1); // para exclusão mútua. 1 livre, 0 block

    // Variáveis
    static int sleep = 0; // diz se o barbeiro está dormindo ou não (1 - dormindo, 0 - acordado)
    static int waiting = 0; // número de clientes esperando nas cadeiras, variavel que vai na seção crítica
    static int total = 0; // total de clientes presentes atualmente (incluindo quem está recebendo o corte)
    static String custGettingHaircut = ""; // nome do cliente recebendo o corte
    static int chairs = 5; // Número de cadeiras disponíveis na sala de espera
    static int n_cust = 5; // Número de clientes que vão ser gerados
    static int totalCustomersServed = 0; // sair do código
    static ArrayList<String> waitingList = new ArrayList<>(); // Lista de clientes esperando
    // Variável para indicar que a barbearia está fechada
    static boolean barbershopClosed = false;

    // Função do Barbeiro
    static void barber() {
        while (true) {
            System.out.println("Barbeiro esperando mais clientes, total de clientes esperando: " + waiting + "\n");

            // tem cliente na loja esperando
            if (waitingList.size() > 0) {
                System.out.println("Clientes esperando são: " + waitingList);
            }
            //não tem clientes e barbeiro pode dormir
            if (waiting == 0 && total == 0) {
                System.out.println("Barbeiro dorme");
                sleep = 1;
            }
            try {
                customers.acquire(); // Aguarda até que um cliente esteja presente (decrementa o semáforo customers). -1
                mutex.acquire(); // entra na seção critica.

                if (waiting > 0) {
                    waiting -= 1; // vai atender o cliente e já descontar da fila de espera
                }

                barbers.release(); // Libera o semáforo do barbeiro, indicando que o barbeiro está disponível. incrementa +1
                mutex.release(); // libera seção critica
                Thread.sleep(1000); // espera por 1 segundo
                cutHair(); // barbeiro vai cortar o cabelo
                Thread.sleep(4000); // cortando por 4 segundos
                System.out.println("Barbeiro terminou o corte de " + custGettingHaircut + "\n");

                // remove a pessoa que já cortou o cabelo da lista de espera
                if (waitingList.size() > 0 && waitingList.contains(custGettingHaircut)) {
                    waitingList.remove(custGettingHaircut);
                }

                custGettingHaircut = ""; // reseta variável de quem está tendo o cabelo cortado
                total -= 1; // menos um cliente

                // Verifica se todos os clientes foram atendidos e fecha a barbearia
                if (total == 0 && barbershopClosed) {
                    System.out.println("Barbearia fechada. Barbeiro vai para casa.");
                    return;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Função do Cliente
    static void customer(String name) {
        try {
            mutex.acquire(); // entra na seção crítica
            if (waiting < chairs || total == 0) { //verifica se n tem clientes e se tem cadeira disponível para esperar
                if (total == 0) { //n tem clientes na barberia
                    total += 1; // incrementa total
                    System.out.println("Cliente: " + name + " entrou na barbearia\n");
                } else { //tem cliente já
                    total += 1;
                    waiting += 1; // incrementa número de clientes esperando
                    waitingList.add(name); // adiciona nome do cliente na lista de espera
                    System.out.println("Cliente: " + name + " entrou na sala de espera, total de clientes esperando: " + waiting + "\n");
                }
                customers.release(); // Libera o semáforo customers, indicando que há um cliente presente na barberia
                mutex.release(); // libera seção crítica.
                barbers.acquire(); //  Aguarda até que o barbeiro esteja disponível (decrementa o semáforo barbers). -1
                //se o barbeiro estiver dormindo, cliente acorda ele
                if (sleep == 1) {
                    System.out.println("Cliente: " + name + " está acordando o barbeiro\n");
                    sleep = 0;
                }
                custGettingHaircut = name;//nomeia quem esta cortando o cabelo
                getHairCut(name);
            } else {
                mutex.release(); // libera seção crítica
                balk(name); // cliente deixa a loja
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Função para cortar cabelo
    static void cutHair() {
        System.out.println("Barbeiro cortando o cabelo de " + custGettingHaircut + "\n");
    }

    // Função para receber corte de cabelo
    static void getHairCut(String name) {
        System.out.println("Cliente: " + name + " está tendo seu cabelo cortado\n");
    }

    // Função para deixar a loja
    static void balk(String name) {
        System.out.println("Cliente: " + name + " está tentando entrar na sala de espera\nloja cheia, então ele sai da loja\n");
    }

    public static void main(String[] args) {
        Thread barberThread = new Thread(BarberShop::barber);// Cria uma thread para o barbeiro.
        barberThread.setName("Barbeiro");// Define o nome da thread do barbeiro.
        barberThread.start();

         Thread[] custThreads = new Thread[n_cust];//Cria um array de threads para os clientes

        while (true) {
            if (waiting == 0 && custGettingHaircut.equals("")) {//Verifica se não há clientes esperando e nenhum cliente está sendo atendido. INICIALIZAÇÃO
                System.out.println("Cliente recebendo o corte: " + custGettingHaircut +
                        ", waiting_list: " + waitingList);//GARANTINDO QUE TA TUDO VAZIO

                System.out.println("Gerando clientes...");

                for (int i = 0; i < n_cust; i++) {
                    int index = i;
                    try{
                        Thread.sleep(100);// espera 1 segundo
                        Thread customerThread = new Thread(() -> customer("Customer" + (index + 1)));//cria a thread do cliente e passa o "nome" do cliente
                        customerThread.setName("Cliente");//Define o nome da thread do cliente.
                        customerThread.start();
                        custThreads[index]= customerThread;
                        Thread.sleep(1000);// espera para criar o proximo cliente
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Vai encerrando as threads dos clientes
                for (Thread ct : custThreads) {
                    try {
                        System.out.println("Aguardando a thread do cliente terminarem");
                        ct.join();
                        System.out.println("Thread do cliente encerrada.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                // Fecha a barbearia após a geração de clientes
                barbershopClosed = true;
            }

            break; // Saia do loop após gerar os clientes
        }

        // Aguarde até que a thread do barbeiro seja encerrada antes de sair.
        try {
            System.out.println("Aguardando a thread do barbeiro terminar...");
            barberThread.join();
            System.out.println("Thread do barbeiro encerrada. Saindo do programa.");
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}