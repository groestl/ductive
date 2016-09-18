package ductive.parse.locators;

public class LookupPosMapping implements PosMapping {

	private int[] offsets;

	public LookupPosMapping(int[] offsets) {
		this.offsets = offsets;
	}

	@Override
	public long map(long pos) {
		if(offsets.length==0)
			return pos;
		int idx = Math.toIntExact(pos);
		if(idx>=offsets.length) {
			int overflow = idx - offsets.length + 1;
			return offsets[offsets.length-1] + overflow;
		}
		return offsets[idx];
	}

}