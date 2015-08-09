package br.com.iejb.sgi.customSearch;

public enum EnumCustomDirection {

	LEFT(1),
	BOTH(2),
	RIGHT(3);

    private final int direction;
    private EnumCustomDirection(int direction) {
        this.direction = direction;
    }		
}
