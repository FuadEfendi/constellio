package com.constellio.app.modules.rm.wrappers.structures;

import java.util.StringTokenizer;

import com.constellio.model.entities.schemas.ModifiableStructure;
import com.constellio.model.entities.schemas.StructureFactory;

public class DecomListContainerDetailFactory implements StructureFactory {

	private static final String NULL = "~null~";

	@Override
	public ModifiableStructure build(String string) {
		StringTokenizer stringTokenizer = new StringTokenizer(string, ":");

		DecomListContainerDetail decomListContainerDetail = new DecomListContainerDetail();
		decomListContainerDetail.setContainerRecordId(readString(stringTokenizer));
		decomListContainerDetail.setFull(Boolean.valueOf(readString(stringTokenizer)));
		decomListContainerDetail.dirty = false;
		return decomListContainerDetail;
	}

	@Override
	public String toString(ModifiableStructure structure) {

		DecomListContainerDetail decomListContainerDetail = (DecomListContainerDetail) structure;
		StringBuilder stringBuilder = new StringBuilder();
		writeString(stringBuilder, "" + (decomListContainerDetail.getContainerRecordId() == null ?
				NULL :
				decomListContainerDetail.getContainerRecordId()));
		writeString(stringBuilder, "" + decomListContainerDetail.isFull() == null ?
				String.valueOf(false) :
				String.valueOf(decomListContainerDetail.isFull()));
		return stringBuilder.toString();
	}

	private String readString(StringTokenizer stringTokenizer) {
		String value = stringTokenizer.nextToken();
		if (NULL.equals(value)) {
			return null;
		} else {
			return value.replace("~~~", ":");
		}
	}

	private void writeString(StringBuilder stringBuilder, String value) {
		if (stringBuilder.length() != 0) {
			stringBuilder.append(":");
		}
		if (value == null) {
			stringBuilder.append(NULL);
		} else {
			stringBuilder.append(value.replace(":", "~~~"));
		}
	}
}
