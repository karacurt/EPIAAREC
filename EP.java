import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EP {
  static Map mapResp;
  static Explorer explorerResp = new Explorer();

  public static void main(String[] args) {

    String mapFile = args[0];
    int op = Integer.parseInt(args[1]);

    mapResp = new Map(mapFile);
    Map map = new Map(mapFile);
    // mapResp.info(); //informacoes do mapa

    if (map.valid() && op >= 1 && op <= 3) {
      Explorer explorer = new Explorer();
      walk(mapResp.getInit()[0], mapResp.getInit()[1], op, map, explorer);// funcionamento
      explorerResp.printSaved();// resposta
    } else {
      System.out.println("Mapa ou criterio inválido");
    }
  }

  static boolean wrongPath = false;

  public static void walk(int x, int y, int op, Map map, Explorer explorer) {
    if (map.getStep(x, y).equals("P"))
      map.setWrongPath(true); // nao passar pelo inicio duas vezes
    if (!map.isFree(x, y))
      return; // Verifica a posição e valida
    int[] finalPosition = map.getFinalPos(); // Armazena a posicao final

    // SETA INFORMACOES DO CAMINHO
    String backupPos = explorer.getPos(); // backtrack
    double bckpTime = explorer.getTime(); // backtrack
    int[] backupInfo = explorer.getInfo(); // backtrack

    String backupPath = explorer.walkStep(x, y); // Anda uma casa

    if (map.hasItem(x, y))
      explorer.grabItem(map.getItemValue(x, y), map.getItemWeight(x, y), x, y); // pegar itens (caso tenha)
    map.fillStep(x, y); // Marcar caminho que já fez

    // map.printRuningMap(); //Printar o mapa andando (com backtrack)

    // VERIFICA SE CHEGOU AO DESTINO
    if (y == finalPosition[1] && x == finalPosition[0]) {
      // Reseta o caminho e volta para a partida
      if (!map.getWrongPath()) {
        if (explorer.isBetter(op)) {
          explorerResp = explorer;
        }
        explorer.reset();
        map.update();

      } else
        return;

    } else {
      // Andar para todas as direcoes
      if (map.canWalk(x + 1, y, wrongPath))
        walk(x + 1, y, op, map, explorer);
      if (map.canWalk(x - 1, y, wrongPath))
        walk(x - 1, y, op, map, explorer);
      if (map.canWalk(x, y - 1, wrongPath))
        walk(x, y - 1, op, map, explorer);
      if (map.canWalk(x, y + 1, wrongPath))
        walk(x, y + 1, op, map, explorer);

      map.forgetPath(x, y); // backtracking
      explorer.forgetPath(backupPath, backupPos, bckpTime, backupInfo); // backtracking

    } // else
  }// void walk
}// public class EP

class Explorer {
  private int weight = 0;
  private double time = 0;
  private int nItems = 0;
  private String itemsPos = "";
  private String path = new String();
  private int value = 0;
  private int nSteps = 0;
  private String pathSaved = "";
  private int savedValue = 0;
  private int savedNItems = 0;
  private int savedSteps = -1;
  private double savedTime = 0;
  private String backupPath = "";
  private int oldSteps = 0;
  private double oldTime = 0;
  private int oldWeight = 0;
  private int oldValue = 0;
  private int oldItems = 0;
  private String oldPos = "";

  // CONTROLERS
  void savePath() {
    this.pathSaved = this.nSteps + " " + this.time + "\n";
    this.pathSaved += this.path;
    this.pathSaved += this.nItems + " " + this.value + " " + this.weight + "\n";
    this.pathSaved += this.itemsPos;
    this.savedValue = this.value;
    this.savedNItems = this.nItems;
    this.savedSteps = this.nSteps;
    this.savedTime = this.time;
  }

  void reset() {
    this.weight = 0;
    this.time = 0;
    this.nItems = 0;
    this.value = 0;
    this.nSteps = 0;
    this.itemsPos = "";
    this.path = this.path.charAt(0) + " " + this.path.charAt(2) + "\n";
  }

  boolean isBetter(int op) {
    switch (op) {
    case 1: // CAMINHO MAIS CURTO
      if (this.getSavedSteps() > this.getSteps() || this.getSavedSteps() == -1) {
        this.savePath();
        return true;
      }
      break;
    case 2: // CAMINHO MAIS VALIOSO
      if (this.getValue() > this.getSavedValue() || this.getSavedSteps() == -1) { // criar metodo path is better
        this.savePath();
        return true;
      }
      break;
    case 3: // CAMINHO MAIS RAPIDO
      if (this.getTime() < this.getSavedTime() || this.getSavedSteps() == -1) {
        this.savePath();
        return true;
      }
      break;
    }
    return false;
  }

  // SETTERS
  String walkStep(int x, int y) {
    String backup = this.path;
    this.backupPath = this.path;
    this.oldSteps = this.nSteps;
    this.oldTime = this.time;

    if (nSteps == 0) {
      this.path = x + " " + y + "\n";
    } else {
      this.path += x + " " + y + "\n";
      this.time += Math.pow((1 + (double) this.weight / 10), 2);
    }
    this.nSteps++;
    return backup;
  }

  void grabItem(int value, int weight, int x, int y) {
    this.oldValue = this.value;
    this.oldWeight = this.weight;
    this.oldItems = this.nItems;
    this.oldPos = this.itemsPos;

    if (nItems == 0)
      this.itemsPos = x + " " + y;
    else
      this.itemsPos += "\n" + x + " " + y;
    this.nItems++;
    this.value += value;
    this.weight += weight;
  }

  void forgetPath(String backupPath, String bkpPos, double time, int[] info) {
    this.path = backupPath;
    this.value = info[3];
    this.nItems = info[0];
    this.nSteps = info[1];
    this.time = time;
    this.weight = info[2];
    this.itemsPos = bkpPos;
  }

  // GETTERS
  /*
   * int [] backupInfo(){ int[] resp = new int[4]; resp[0] = this.nItems; resp[1]
   * = this.nSteps; }
   */
  int[] getInfo() {
    int[] info = new int[4];
    info[0] = this.nItems;
    info[1] = this.nSteps;
    info[2] = this.weight;
    info[3] = this.value;
    return info;
  }

  String getPos() {
    return this.itemsPos;
  }

  double getTime() {
    return this.time;
  }

  int getSteps() {
    return this.nSteps;
  }

  int getNItems() {
    return this.nItems;
  }

  int getValue() {
    return this.value;
  }

  double getSavedTime() {
    return this.savedTime;
  }

  int getSavedNItems() {
    return this.savedNItems;
  }

  int getSavedValue() {
    return this.savedValue;
  }

  int getSavedSteps() {
    return this.savedSteps;
  }

  // PRINTERS
  void printSaved() {
    System.out.println(this.pathSaved);
  }

  void print() {
    System.out.println("TAMANHO DO CAMINHO ENCONTRADO: " + this.nSteps);
    System.out.println("TEMPO PARA PERCORRER: " + this.time);
    System.out.println("CAMINHO PERCORRIDO:");
    System.out.println(this.path);
    System.out.println("QUANTIDADE DE ITEMS COLETADOS: " + this.nItems);
    System.out.println("VALOR TOTAL DOS INTENS: " + this.value);
    System.out.println("PESO TOTAL DOS ITENS: " + this.weight);
    System.out.println(this.itemsPos);
  }

}

class Map {

  private Items[] items;
  private int row;
  private int col;
  private String[][] map;
  private String[][] runingMap;
  private int[] initPosition = new int[2];
  private int[] actualPosition = new int[2];
  private int[] finalPosition = new int[2];
  private boolean wrongPath = false;

  // CONTROLERS
  void update() {
    this.actualPosition[0] = this.initPosition[0];
    this.actualPosition[1] = this.initPosition[1];
    this.runingMap[this.finalPosition[0]][this.finalPosition[1]] = "D";
    this.runingMap[this.initPosition[0]][this.initPosition[1]] = "P";
  }

  // SETTERS
  void forgetPath(int x, int y) {
    this.runingMap[x][y] = this.map[x][y];
  }

  void setWrongPath(boolean bool) {
    this.wrongPath = true;
  }

  void fillStep(int x, int y) {
    if (this.runingMap[x][y].equals("C") || this.runingMap[x][y].equals("P")) {
      this.runingMap[x][y] = "P";
      return;
    }
    if (!this.runingMap[x][y].equals("D"))
      this.runingMap[x][y] = "E";
  }

  // VERIFIERS
  boolean valid() {
    if (this.map != null)
      return true;
    else
      return false;
  }

  boolean isFree(int x, int y) {
    if (x > this.row - 1 || y > this.col - 1 || x < 0 || y < 0 || this.map[x][y].equals("X")
        || this.runingMap[x][y].equals("E")) {
      return false;
    } else {
      if (this.wrongPath) {
        return false;
      }
      return true;
    }
  }

  boolean hasItem(int x, int y) {
    if (this.map[x][y].equals("I"))
      return true;
    else
      return false;
  }

  boolean canWalk(int x, int y, boolean wrongPath) {
    if (this.getStep(x, y).equals("E") || this.getStep(x, y).equals("X") || x < 0 || y < 0 || x > this.row - 1
        || y > this.col - 1) {
      return false;
    } else {
      if (this.wrongPath)
        this.wrongPath = false;
      return true;
    }

  }

  // GETTERS
  boolean getWrongPath() {
    return this.wrongPath;
  }

  int[] getActualPos() {
    return this.actualPosition;
  }

  int[] getFinalPos() {
    return this.finalPosition;
  }

  String[][] getMap() {
    return this.runingMap;
  }

  int getCol() {
    return this.col;
  }

  int getRow() {
    return this.row;
  }

  int[] getInit() {
    return this.initPosition;
  }

  int getItemValue(int x, int y) {
    for (int i = 0; i < this.items.length; i++) {
      if (this.items[i].getCol() == y && this.items[i].getRow() == x)
        return this.items[i].getValue();
    }
    return -1;
  }

  int getItemWeight(int x, int y) {
    for (int i = 0; i < this.items.length; i++) {
      if (this.items[i].getCol() == y && this.items[i].getRow() == x)
        return this.items[i].getWeight();
    }
    return -1;
  }

  String getStep(int x, int y) {
    if (x >= 0 && y >= 0 && x <= this.row - 1 && y <= this.col - 1)
      return this.runingMap[x][y];
    else
      return "X";
  }

  // PRINTERS
  void printRuningMap() {
    System.out.println("DESENHO DO MAPA:");

    for (int r = 0; r < row; r++) {
      for (int c = 0; c < col; c++) {
        System.out.print("\t" + this.runingMap[r][c]);
      }
      System.out.println();
    }
    System.out.println("LEGENDA:");
    System.out.println("  I - item");
    System.out.println("  X - Bloqueado");
    System.out.println("  P - Partida");
    System.out.println("  D - Destino");
    System.out.println("  E - Destino");
    System.out.println("POSICAO: X = " + this.actualPosition[0] + " Y = " + this.actualPosition[1]);
  }

  void printMap() {
    System.out.println("DESENHO DO MAPA:");

    for (int r = 0; r < row; r++) {
      for (int c = 0; c < col; c++) {
        System.out.print("\t" + this.map[r][c]);
      }
      System.out.println();
    }
    System.out.println("LEGENDA:");
    System.out.println("  I - item");
    System.out.println("  X - Bloqueado");
    System.out.println("  P - Partida");
    System.out.println("  D - Destino");
  }

  void info() {
    System.out.println("INFORMAÇÕES SOBRE O MAPA");
    System.out.println("linhas: " + this.row);
    System.out.println("colunas: " + this.col);
    System.out.println("DESENHO DO MAPA:");

    for (int r = 0; r < row; r++) {
      for (int c = 0; c < col; c++) {
        System.out.print("\t" + map[r][c]);
      }
      System.out.println();
    }
    System.out.println("LEGENDA:");
    System.out.println("  I - item");
    System.out.println("  X - Bloqueado");
    System.out.println("  P - Partida");
    System.out.println("  D - Destino");

    System.out.println("Quantidade de Itens: " + this.items.length);
    for (int i = 0; i < this.items.length; i++) {
      System.out.println("\t-ITEM " + (i + 1));
      items[i].print();
    }

    System.out.println("PARTIDA: x = " + this.initPosition[0] + " Y = " + this.initPosition[1]);
    System.out.println("CHEGADA: x = " + this.finalPosition[0] + " Y = " + this.finalPosition[1]);
  }

  // CONSTRUCTOR
  Map(String mapFile) {
    String[][] map;
    int row = 0, col = 0, nItems = 0;
    Items[] items;
    int[] initPosition = new int[2];
    int[] finalPosition = new int[2];

    try {
      FileReader file = new FileReader(mapFile);
      BufferedReader readFile = new BufferedReader(file);

      String line = readFile.readLine();

      // defiir linha e coluna do mapa
      String[] data = line.split(" ");
      row = Integer.parseInt(data[0]);
      col = Integer.parseInt(data[1]);
      map = new String[row][col];
      line = readFile.readLine();

      // inserindo valores no mapa
      for (int r = 0; r < row; r++) {
        String[] Aux = line.split(" ");
        String[] dataAux = Aux[0].split("");
        for (int c = 0; c < col; c++) {
          map[r][c] = dataAux[c];
        }
        line = readFile.readLine();
      }

      // ITENS
      String[] dataAux = line.split(" ");
      nItems = Integer.parseInt(dataAux[0]);
      items = new Items[nItems];

      for (int i = 0; i < items.length; i++) {
        items[i] = new Items();
        line = readFile.readLine();
        String[] aux = line.split(" ");
        int[] dataItems = new int[4];
        for (int j = 0; j < dataItems.length; j++)
          dataItems[j] = Integer.parseInt(aux[j]);
        items[i].set(dataItems[0], dataItems[1], dataItems[2], dataItems[3]);
      }

      line = readFile.readLine();

      // setar posicao inicial e final
      String[] dataPosition = line.split(" ");
      for (int i = 0; i < dataPosition.length; i++)
        initPosition[i] = Integer.parseInt(dataPosition[i]);
      line = readFile.readLine();
      dataPosition = line.split(" ");
      for (int i = 0; i < dataPosition.length; i++)
        finalPosition[i] = Integer.parseInt(dataPosition[i]);

      // INSERIR INFORMACOES NO OBJETO MAPA

      line = readFile.readLine();

      file.close();

      this.row = row;

      this.col = col;

      this.map = map;

      this.initPosition = initPosition;

      this.finalPosition = finalPosition;

      this.actualPosition = initPosition;

      this.items = items;

      // INSERINDO ITENS NO MAPA

      for (int i = 0; i < nItems; i++) {
        this.map[items[i].getRow()][items[i].getCol()] = "I";
      }
      // INSERINDO PARTIDA E DESTINO

      this.map[initPosition[0]][initPosition[1]] = "C";
      this.map[finalPosition[0]][finalPosition[1]] = "D";

      this.runingMap = new String[row][col];
      for (int r = 0; r < row; r++) {
        for (int c = 0; c < col; c++) {
          this.runingMap[r][c] = this.map[r][c];
        }
      }

      this.map[initPosition[0]][initPosition[1]] = "P";
    } catch (IOException e) {
      System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
    }
  } // construtormap
}// class map

class Items {
  private int row;
  private int col;
  private int value;
  private int weight;

  void set(int row, int col, int value, int weight) {
    this.row = row;
    this.col = col;
    this.value = value;
    this.weight = weight;
  }

  void print() {
    System.out.println("\t\tlinha: " + row);
    System.out.println("\t\tcoluna: " + col);
    System.out.println("\t\tvalor: " + value);
    System.out.println("\t\tpeso: " + weight);
  }

  int getRow() {
    return this.row;
  }

  int getWeight() {
    return this.weight;
  }

  int getValue() {
    return this.value;
  }

  int getCol() {
    return this.col;
  }
}
