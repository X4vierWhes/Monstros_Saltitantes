package org.example;

/**
 * Classe principal que inicializa a aplicação.
 *
 * <p>Este é o ponto de entrada do programa. Ele cria uma instância da classe
 * {@link Screen} e chama o método {@code initScreen()} para iniciar a interface.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see Screen
 */
public class Main {

    /**
     * Método principal que inicia a execução da aplicação.
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        Screen screenPointer = new Screen();
        screenPointer.initScreen();
    }
}
