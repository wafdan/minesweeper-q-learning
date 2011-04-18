package rl;


public interface RLWorld {
	
	// Mengembalikan array yg mengandung informasi
	// banyaknya state dalam setiap dimensi ( [0] - [array.length - 2] )
	// dan banyaknya aksi yg mungkin ( [array.length - 1] ).
	int[] getDimension();
	
	// Mengembalikan instance terbaru dari state yg dihasilkan
	// dari melakukan aksi terhadap state terkini. 
	short[] getNextState(int x, int y);
	
	// Mengembalikan nilai reward terbaru
	// yang didapat dari getNextState(int action).
	double getReward();
	
	// Mengembalikan TRUE jika aksi yg diberikan merupakan 
	// aksi yg valid dalam state terkini,
	// FALSE jika tidak.
	boolean validAction(int action);
	
	// Mengembalikan TRUE jika aksi terkini merupakan absorbing state, FALSE jika tidak.
	boolean endState();
	
	// Mereset state menjadi state awal dan mengembalikan state tersebut.
	short[] resetState();
	
	// Mengembalikan initial value untuk policy.
	double getInitValues();
}
