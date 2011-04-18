package rl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Generator {

    int mine_map[][];
    short array_genmap[][]; // array of generated map retrieved from dbase
    int mmm;
    int r;
    int c;
    private static Generator gen;

    public static Generator getGenerator() {
        if (gen == null) // it's ok, we can call this constructor
        {
            gen = new Generator();
        }
        return gen;
    }

    public Generator() {
        mine_map = null;
        array_genmap = null;
        mmm = 0;
        r = 0;
        c = 0;
    }

    public Generator(int x, int y, int mines) {
        mine_map = new int[x][y];
        // array_genmap = null;
        mmm = mines;
        r = x;
        c = y;
    }

    public void setGenerator(int x, int y, int mines) {
        mine_map = new int[x][y];
        // array_genmap = null;
        mmm = mines;
        r = x;
        c = y;
    }

    public String formatMinemap() {
        String str = "";
        for (int y = 0; y < r; y++) {
            for (int x = 0; x < c; x++) {
                if (mine_map[y][x] < 0) {
                    str += "X";
                    // System.out.print("X");
                } else if (mine_map[y][x] == 9) {
                    str += "?";
                } else {
                    // cad = String.valueOf((mine_map[y][x]));
                    str += String.valueOf(mine_map[y][x]);
                    // System.out.print((mine_map[y][x]));
                }
            }
        }
        // System.out.print(str);
        return str;
    }

    public int pick(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        int p = (int) Math.floor((double) n * Math.random());
        return p >= n ? p - n : p;
    }

    // public void generateMap(int rows, int columns, int mines) {
    // mmm = mines;
    // r = rows;
    // c = columns;
    // }
    public void generateMap() {

        mine_map = new int[r][c];

        for (int y = 0; y < r; y++) {
            for (int x = 0; x < c; x++) {
                mine_map[y][x] = 0;
            }
        }

        if (mmm / 2 >= r * c) // Odd parameters
        {
            return;
        } else {
            for (int k = mmm; k > 0;) { // Place mines randomly
                int x = pick(c);
                int y = pick(r);
                if (mine_map[y][x] >= 0) {
                    mine_map[y][x] = -1;
                    k--;
                }
            }

            for (int y = 0; y < r; y++) // Compute weights
            {
                for (int x = 0; x < c; x++) {
                    if (mine_map[y][x] >= 0) {
                        int w = 0;
                        int y0 = Math.max(0, y - 1);
                        int y1 = Math.min(r, y + 2);
                        int x0 = Math.max(0, x - 1);
                        int x1 = Math.min(c, x + 2);
                        for (int yw = y0; yw < y1; yw++) {
                            for (int xw = x0; xw < x1; xw++) {
                                if (mine_map[yw][xw] < 0) {
                                    w++;
                                }
                            }
                        }
                        mine_map[y][x] = w;
                    }
                }
            }
        }
    }

    public short[] generateStateOfMap(short[] map) {
        short[] state = new short[map.length];
        if (map != null) {
        }

        return state;
    }

    public short[] getSingleMap(int idx) {
        this.retrieveMap();
        return array_genmap[idx];
    }

    // public void retrieveMap(int rows, int columns, int mines) {}
    public void retrieveMap() {
        int rows = r;
        int columns = c;
        int mines = mmm;
        try {
            String tablename = "minemap_" + rows + "x" + columns + "_" + mines;
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/C:/sqlite/test.db");

            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("select * from " + tablename + ";");
            int idx = 0;
            while (rs.next()) {
                idx = rs.getRow();
            }

            System.out.println("Num of rows : " + idx);

            idx--;
            // initiate array_genmap
            array_genmap = new short[idx][rows * columns];

            for (int y = 0; y < idx; y++) {
                for (int x = 0; x < rows * columns; x++) {
                    array_genmap[y][x] = 0;
                }
            }

            // reexecute query karena ResultSet bertipe TYPE_FORWARD_ONLY
            rs = stat.executeQuery("select * from " + tablename + ";");

            idx = 0;
            while (rs.next()) {
                String temp = rs.getString("config");
                System.out.println(temp);
                for (int y = 0; y < rows * columns; y++) {
                    // System.out.println(temp.charAt(y));
                    // String temp2 = temp.substring(y, y+1);
                    char ctemp = temp.charAt(y);
                    if (ctemp == 'X') {
                        array_genmap[idx][y] = -1;
                    } else {
                        array_genmap[idx][y] = (short) (ctemp - 48);
                    }
                }
                idx++;
                // System.out.println("map = "+rs.getString("config"));
                // System.out.println("map " + (++idx));
                // for (int y = 0; y < rows; y++) {

            }
            rs.close();
            conn.close();

            System.out.print("Contents of array_genmap:");
            for (int y = 0; y < array_genmap.length; y++) {
                for (int x = 0; x < array_genmap[y].length; x++) {
                    System.out.print(array_genmap[y][x]);
                }
                System.out.println();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveGenerateMap(int rows, int columns, int mines) {
        try {
            String tablename = "minemap_" + rows + "x" + columns + "_" + mines;
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/C:/sqlite/test.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("drop table if exists " + tablename + ";");
            stat.executeUpdate("create table " + tablename
                    + " (config text primary key);");
            PreparedStatement prep = conn.prepareStatement("replace into "
                    + tablename + " values(?);");

            // Bangkitkan map sebanyak beberapa kali, entahlah berapa pastinya
            for (int i = 0; i < 9999; i++) {
                generateMap();
                prep.setString(1, formatMinemap());
                prep.addBatch();
            }
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);

            // Sekedar menampilkan ke layar console
            ResultSet rs = stat.executeQuery("select * from " + tablename + ";");
            int idx = 0;
            while (rs.next()) {
                // System.out.println("map = "+rs.getString("config"));
                System.out.println("map " + (++idx));
                for (int y = 0; y < rows; y++) {
                    System.out.println(rs.getString("config").substring(
                            columns * y, columns * (y + 1)));
                }
                System.out.println("------");
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//	public void generateState(int rows, int columns, int mines, int cover) {
//		mmm = mines;
//		r = rows;
//		c = columns;
//	}
    public void generateState(int cover) {
        //mine_map = new int[r][c];
//
//		for (int y = 0; y < r; y++)
//			for (int x = 0; x < c; x++) {
//				mine_map[y][x] = 0;
//			}
//
//		if (mmm / 2 >= r * c) // Odd parameters
//			return;
//		else {
//			for (int k = mmm; k > 0;) { // Place mines randomly
//				int x = pick(c);
//				int y = pick(r);
//				if (mine_map[y][x] >= 0) {
//					mine_map[y][x] = -1;
//					k--;
//				}
//			}
//
//			for (int y = 0; y < r; y++)
//				// Compute weights
//				for (int x = 0; x < c; x++)
//					if (mine_map[y][x] >= 0 && mine_map[y][x] != 9) {
//						int w = 0;
//						int y0 = Math.max(0, y - 1);
//						int y1 = Math.min(r, y + 2);
//						int x0 = Math.max(0, x - 1);
//						int x1 = Math.min(c, x + 2);
//						for (int yw = y0; yw < y1; yw++)
//							for (int xw = x0; xw < x1; xw++)
//								if (mine_map[yw][xw] < 0)
//									w++;
//						mine_map[y][x] = w;
//					}
//		}

        // place cover randomly
        for (int j = cover; j > 0;) {
            int x = pick(c);
            int y = pick(r);
            // if (mine_map[y][x] == 0) {
            mine_map[y][x] = 9;
            j--;
            // }
        }

    }

    public void tryGenerateState(int row, int columns) {
        int dim = row * columns;
        ArrayList<Integer> arlist = new ArrayList<Integer>();
        int ii = 0;
        int i = 0;

        for (ii = 0; ii < dim; ii++) {
            int now = ii;
            for (i = ii; i < dim; i++) {
                arlist.add(i);
            }
        }
    }

    // public void saveGenerateState(int rows, int columns, int mines) {}
    public void saveGenerateState() {
        int rows = r;
        int columns = c;
        int mines = mmm;
        try {
            String tablename = "minestate_" + rows + "x" + columns + "_"
                    + mines;
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/C:/sqlite/test.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("drop table if exists " + tablename + ";");
            stat.executeUpdate("create table " + tablename
                    + " (config text primary key);");
            PreparedStatement prep = conn.prepareStatement("replace into "
                    + tablename + " values(?);");

            generateMap();

            System.out.println("The map:");
            for (int hoo = 0; hoo < r; hoo++) {
                for (int hooo = 0; hooo < c; hooo++) {
                    System.out.print(mine_map[hoo][hooo]);
                }
                System.out.println();
            }
            System.out.println("|----------|");

            /// Bangkitkan cover sebanyak beberapa kali, entahlah berapa pastinya
            ///for (int i = 0; i < 9999; i++) {
//                for (int j = 1; j < rows * columns - 1; j++) {
//                    for (int k = 0; k < 9999; k++) {
//                        generateState(j);
//                        prep.setString(1, formatMinemap());
//                        prep.addBatch();
//                    }
//                }
            ///}

            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);

            // Sekedar menampilkan ke layar console
            ResultSet rs = stat.executeQuery("select * from " + tablename + ";");
            int idx = 0;
            while (rs.next()) {
                // System.out.println("map = "+rs.getString("config"));
                System.out.println("state " + (++idx));
                for (int y = 0; y < rows; y++) {
                    System.out.println(rs.getString("config").substring(
                            columns * y, columns * (y + 1)));
                }
                System.out.println("------");
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void doDbase() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/C:/sqlite/test.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("drop table if exists people;");
            stat.executeUpdate("create table people (name, occupation);");
            PreparedStatement prep = conn.prepareStatement("insert into people values(?,?);");

            prep.setString(1, "Mino");
            prep.setString(2, "Programmer");
            prep.addBatch();
            prep.setString(1, "Lightning");
            prep.setString(2, "Soldier");
            prep.addBatch();

            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);

            ResultSet rs = stat.executeQuery("select * from people;");
            while (rs.next()) {
                System.out.println("name = " + rs.getString("name"));
                System.out.println("job = " + rs.getString("occupation"));
            }
            rs.close();

            rs = stat.executeQuery("select * from tbl1;");
            while (rs.next()) {
                System.out.println("col1 = " + rs.getString("one"));
                System.out.println("col2 = " + rs.getString("two"));
            }
            rs.close();

            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Object clone()
            throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
        // that'll teach 'em
    }
}
