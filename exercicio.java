import java.util.Scanner;

public class exercicio {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Digite o tamanho da matriz (linha x coluna):");
        // int lin = sc.nextInt();
        // int col = sc.nextInt();
        String[][] area = new String[5][5];

        System.out.println("Digite o x, y de partida:");

        int xpartida = sc.nextInt();
        int ypartida = sc.nextInt();

        System.out.println("Digite o x, y de chegada:");
        int xalvo = sc.nextInt();
        int yalvo = sc.nextInt();

        for (int l = 0; l < area.length; l++) {
            for (int c = 0; c < area.length; c++) {
                area[l][c] = ".";
            }
        }
        area[xalvo][yalvo] = "X";
        area[xpartida][ypartida] = "P";
        System.out.println();
        printma(area);

        while (true) {

            if (xpartida < xalvo) {
                xpartida++;
                area[xpartida][ypartida] = "p";
                continue;
            }

            if (xpartida > xalvo) {
                xpartida--;
                area[xpartida][ypartida] = "p";

                continue;
            }

            if (ypartida < yalvo) {
                ypartida++;
                area[xpartida][ypartida] = "p";
                continue;
            }

            if (ypartida > yalvo) {
                ypartida--;
                area[xpartida][ypartida] = "p";
                continue;
            }

            if (ypartida == yalvo && xpartida == xalvo)
                break;
            printma(area);
            System.out.println();
        } // while
        printma(area);
        System.out.println();
    }// main

    static void printma(String[][] area) {
        for (int l = 0; l < area.length; l++) {
            for (int c = 0; c < area.length; c++) {
                System.out.printf(area[l][c] + " ");
            }
            System.out.println();
        }

    }
}