package com.constellio.sdk.tests;

import static com.constellio.app.ui.i18n.i18n.$;
import static junit.framework.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Condition;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.groups.Tuple;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.constellio.app.modules.rm.wrappers.DecommissioningList;
import com.constellio.app.ui.i18n.i18n;
import com.constellio.data.dao.dto.records.RecordDTO;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.RecordWrapper;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.entries.ManualDataEntry;
import com.constellio.model.entities.security.AuthorizationDetails;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.frameworks.validation.ValidationError;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.frameworks.validation.ValidationRuntimeException;
import com.constellio.model.services.contents.ContentFactory;
import com.constellio.model.services.records.RecordServicesException.ValidationException;
import com.constellio.model.services.records.RecordUtils;
import com.constellio.sdk.tests.setups.SchemaShortcuts;

public class TestUtils {

	public static final String chuckNorris = "Chuck Norris";
	private static Random random = new Random();
	private static int gen;
	public static Comparator<? super DecommissioningList> comparingRecordWrapperIds = new Comparator<DecommissioningList>() {
		@Override
		public int compare(DecommissioningList o1, DecommissioningList o2) {
			return o1.getId().endsWith(o2.getId()) ? 0 : 1;
		}
	};

	private TestUtils() {
	}

	public static String frenchPangram() {
		return "ABCDEFGHIJKLMNOPQRST";
	}

	public static byte[] aByteArray() {
		return aByteArray(2);
	}

	public static byte[] aByteArray(int length) {
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			if (gen >= Byte.MAX_VALUE) {
				gen = 0;
			}
			bytes[i] = Integer.valueOf(++gen).byteValue();
		}
		return bytes;
	}

	public static byte[] aRandomByteArray(int length) {
		byte[] bytes = new byte[length];
		random.nextBytes(bytes);
		return bytes;
	}

	public static LocalDateTime aDateTime() {
		return new LocalDateTime(2000 + random.nextInt(20), random.nextInt(13 - 1) + 1, random.nextInt(29 - 1) + 1,
				random.nextInt(24 - 1) + 1, random.nextInt(60 - 1) + 1);
	}

	public static LocalDate aDate() {
		return new LocalDate(2000 + random.nextInt(20), random.nextInt(13 - 1) + 1, random.nextInt(29 - 1) + 1);
	}

	public static File aFile() {
		return new File(aString());
	}

	public static File aFile(File parent) {
		return new File(parent, aString());
	}

	public static long aLong() {
		return anInteger();
	}

	public static int anInteger() {
		if (gen > 100000000) {
			gen = 0;
		}
		return ++gen;
	}

	public static String aString() {
		return "" + anInteger();
	}

	public static String[] aStringArray() {
		final int maxArraySize = 10;
		String[] theStringArray = new String[maxArraySize];

		for (int i = 0; i < random.nextInt(maxArraySize); i++) {
			theStringArray[i] = aString();
		}

		return theStringArray;
	}

	public static Set<Class<?>> getElementsClasses(Set<?> objects) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (Object o : objects) {
			classes.add(o.getClass());
		}
		return classes;
	}

	public static boolean isIntegrationServer() {
		return new File(".").getAbsolutePath().contains("jenkins");
	}

	@SuppressWarnings("rawtypes")
	public static <T> Condition onlyElementsOfClass(final Class<?>... classes) {

		return new Condition() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean matches(Object value) {
				List<Class<?>> valuesClasses = new ArrayList<>();
				Iterator<T> iterator = ((Iterable) value).iterator();
				while (iterator.hasNext()) {
					valuesClasses.add(iterator.next().getClass());
				}
				try {
					assertThat(valuesClasses).containsOnly(classes);
					return true;
				} catch (Exception e) {
					return false;
				}
			}

		};
	}

	public static void assertThatToEqualsAndToStringThrowNoException(Object o, Object o2) {
		assertThat(o).isEqualTo(o2);
		assertThat(o.toString()).isEqualTo(o2.toString());
		assertThat(o.hashCode()).isEqualTo(o2.hashCode());

		for (Method method : o.getClass().getMethods()) {
			boolean getter = method.getName().startsWith("get") || method.getName().startsWith("is");
			if (getter && method.getGenericParameterTypes().length == 0) {
				try {
					method.invoke(o);
				} catch (IllegalAccessException e) {

				} catch (InvocationTargetException e) {

				}
			}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Condition<? super Object> unmodifiableCollection() {
		return new Condition() {

			@Override
			public boolean matches(Object value) {
				Collection collection = (Collection) value;
				try {
					collection.clear();
					return false;
				} catch (Exception e) {
					return true;
				}
			}

		}.describedAs("unmodifiable");
	}

	public static List<String> ids(List<Record> records) {
		return new RecordUtils().toIdList(records);
	}

	public static String[] idsArray(Record... records) {
		return ids(Arrays.asList(records)).toArray(new String[0]);
	}

	@SafeVarargs
	public static <T> List<T> asList(T... elements) {
		return Arrays.asList(elements);
	}

	public static <K, V> Map<K, V> asMap(K key1, V value1) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key1, value1);
		return map;
	}

	public static <K, V> Map<K, V> asMap(K key1, V value1, K key2, V value2) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static <K, V> Map<K, V> asMap(K key1, V value1, K key2, V value2, K key3, V value3) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static <K, V> Map<K, V> asMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);
		map.put(key4, value4);
		return map;
	}

	public static <K, V> Map<K, V> asMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5,
			V value5) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);
		map.put(key4, value4);
		map.put(key5, value5);
		return map;
	}

	public static <K, V> Map<K, V> asMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5,
			V value5, K key6, V value6) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);
		map.put(key4, value4);
		map.put(key5, value5);
		map.put(key6, value6);
		return map;
	}

	public static Map<String, Object> asStringObjectMap(String key1, Object value1) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key1, value1);
		return map;
	}

	public static Map<String, Object> asStringObjectMap(String key1, Object value1, String key2, Object value2) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static Map<String, Object> asStringObjectMap(String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static Metadata mockMetadata(String code) {
		String localCode = code.split("_")[2];
		Metadata metadata = mock(Metadata.class, code);
		when(metadata.getCode()).thenReturn(code);
		when(metadata.getLocalCode()).thenReturn(localCode);
		return metadata;
	}

	public static Metadata mockManualMultivalueMetadata(String code, MetadataValueType type) {
		Metadata metadata = mockManualMetadata(code, type);
		when(metadata.isMultivalue()).thenReturn(true);
		return metadata;

	}

	public static Metadata mockManualMetadata(String code, MetadataValueType type) {
		String localCode = code.split("_")[2];
		Metadata metadata = mock(Metadata.class, code);
		when(metadata.getCode()).thenReturn(code);
		when(metadata.getLocalCode()).thenReturn(localCode);
		when(metadata.getType()).thenReturn(type);
		if (type == MetadataValueType.CONTENT) {
			when(metadata.getStructureFactory()).thenReturn(new ContentFactory());
		}
		when(metadata.getDataEntry()).thenReturn(new ManualDataEntry());
		return metadata;
	}

	public static List<String> idsOf(List<AuthorizationDetails> details) {
		List<String> ids = new ArrayList<>();
		for (AuthorizationDetails detail : details) {
			ids.add(detail.getId());
		}
		return ids;
	}

	public static List<String> recordsIds(List<Record> records) {
		List<String> ids = new ArrayList<>();
		for (Record record : records) {
			ids.add(record.getId());
		}
		return ids;
	}

	public static List<String> usernamesOf(List<UserCredential> users) {
		List<String> ids = new ArrayList<>();
		for (UserCredential user : users) {
			ids.add(user.getUsername());
		}
		return ids;
	}

	@SafeVarargs
	public static <T> Set<T> asSet(T... elements) {
		return new HashSet<T>(asList(elements));
	}

	public static RecordDTO newRecordDTO(String id, SchemaShortcuts schema) {

		Map<String, Object> fields = new HashMap<>();
		fields.put("collection_s", schema.collection());
		fields.put("schema_s", schema.code());

		return new RecordDTO(id, anInteger(), null, fields);
	}

	public static Condition<? super List<String>> noDuplicates() {
		return new Condition<List<String>>() {
			@Override
			public boolean matches(List<String> list) {
				Set<String> set = new HashSet<>();

				for (String item : list) {
					if (set.contains(item)) {
						fail("Item '" + item + "' was found twice in list");
					} else {
						set.add(item);
					}
				}
				return true;
			}
		};
	}

	public static class MapBuilder<K, V> {

		Map<K, V> map = new HashMap<>();

		public static <K, V> MapBuilder<K, V> with(K key, V value) {
			return new MapBuilder<K, V>().andWith(key, value);
		}

		public MapBuilder<K, V> andWith(K key, V value) {
			map.put(key, value);
			return this;
		}

		public Map<K, V> build() {
			return map;
		}
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static RecordWrapperAssert assertThatRecord(RecordWrapper recordWrapper) {
		return new RecordWrapperAssert(recordWrapper);
	}

	public static RecordsAssert assertThatRecords(List<?> actual) {
		return new RecordsAssert(actual);
	}

	public static RecordAssert assertThatRecord(Record actual) {
		return new RecordAssert(actual);
	}

	public static class RecordAssert extends ObjectAssert<Record> {

		protected RecordAssert(Record actual) {
			super(actual);
		}

		public RecordAssert hasMetadataValue(final Metadata metadata, final Object expectedValue) {
			assertThat(actual).describedAs("record").isNotNull();
			return (RecordAssert) super.has(new Condition<Record>() {
				@Override
				public boolean matches(Record value) {
					assertThat(actual.get(metadata)).as((metadata.getCode())).isEqualTo(expectedValue);
					return true;
				}
			});
		}

		public RecordAssert hasNoMetadataValue(final Metadata metadata) {
			assertThat(actual).describedAs("record").isNotNull();
			return (RecordAssert) super.has(new Condition<Record>() {
				@Override
				public boolean matches(Record value) {

					if (metadata.isMultivalue()) {
						assertThat(actual.getList(metadata)).as((metadata.getCode())).isEmpty();
					} else {

						assertThat(actual.get(metadata)).as((metadata.getCode())).isNull();
					}
					return true;
				}
			});
		}
	}

	public static class RecordsAssert extends ListAssert<Object> {

		protected RecordsAssert(List<?> actual) {
			super((List<Object>) actual);
		}

		public ListAssert<Tuple> extractingMetadatas(Metadata... metadatas) {
			List<Tuple> values = new ArrayList<>();

			for (Object record : actual) {
				Object[] objects = new Object[metadatas.length];
				for (int i = 0; i < metadatas.length; i++) {
					if (record instanceof Record) {
						objects[i] = ((Record) record).get(metadatas[i]);
					} else if (record instanceof RecordWrapper) {
						objects[i] = ((RecordWrapper) record).get(metadatas[i]);
					} else {
						throw new RuntimeException("Unsupported object of class '" + record.getClass());
					}
				}
				values.add(new Tuple(objects));
			}

			return assertThat(values);
		}

	}

	public static class RecordWrapperAssert extends ObjectAssert<RecordWrapper> {

		protected RecordWrapperAssert(RecordWrapper actual) {
			super(actual);
		}

		public RecordWrapperAssert hasMetadata(final Metadata metadata, final Object expectedValue) {
			//return hasMetadata(metadata.getLocalCode(), expectedValue);
			assertThat(actual).isNotNull();
			return (RecordWrapperAssert) super.has(new Condition<RecordWrapper>() {
				@Override
				public boolean matches(RecordWrapper value) {
					assertThat(actual.getWrappedRecord().get(metadata)).as((metadata.getCode())).isEqualTo(expectedValue);
					return true;
				}
			});
		}

		public RecordWrapperAssert hasMetadata(final String metadataLocalCode, final Object expectedValue) {
			return hasMetadata(actual.getSchema().getMetadata(metadataLocalCode), expectedValue);
			//			assertThat(actual).isNotNull();
			//			return (RecordWrapperAssert) super.has(new Condition<RecordWrapper>() {
			//				@Override
			//				public boolean matches(RecordWrapper value) {
			//					Metadata metadata = value.getSchema().getMetadata(metadataLocalCode);
			//					assertThat(actual.getWrappedRecord().get(metadata)).as((metadata.getCode())).isEqualTo(expectedValue);
			//					return true;
			//				}
			//			});
		}
	}

	public static List<Tuple> extractingSimpleCodeAndParameters(ValidationRuntimeException e, String... parameters) {
		return extractingSimpleCodeAndParameters(e.getValidationErrors(), parameters);
	}

	public static List<Tuple> extractingSimpleCodeAndParameters(ValidationException e, String... parameters) {
		return extractingSimpleCodeAndParameters(e.getErrors(), parameters);
	}

	public static List<Tuple> extractingSimpleCodeAndParameters(com.constellio.model.frameworks.validation.ValidationException e,
			String... parameters) {
		return extractingSimpleCodeAndParameters(e.getValidationErrors(), parameters);
	}

	public static List<String> extractingSimpleCode(ValidationErrors errors) {

		List<String> codes = new ArrayList<>();
		for (ValidationError error : errors.getValidationErrors()) {
			codes.add(StringUtils.substringAfterLast(error.getCode(), "."));
		}

		return codes;
	}

	public static List<Tuple> extractingWarningsSimpleCodeAndParameters(ValidationErrors errors, String... parameters) {

		List<Tuple> tuples = new ArrayList<>();
		for (ValidationError error : errors.getValidationWarnings()) {
			Tuple tuple = new Tuple(StringUtils.substringAfterLast(error.getCode(), "."));
			for (String parameter : parameters) {
				tuple.addData(error.getParameters().get(parameter));
			}
			tuples.add(tuple);
		}

		return tuples;
	}

	public static List<Tuple> extractingSimpleCodeAndParameters(ValidationErrors errors, String... parameters) {

		List<Tuple> tuples = new ArrayList<>();
		for (ValidationError error : errors.getValidationErrors()) {
			Tuple tuple = new Tuple(StringUtils.substringAfterLast(error.getCode(), "."));
			for (String parameter : parameters) {
				tuple.addData(error.getParameters().get(parameter));
			}
			tuples.add(tuple);
		}

		return tuples;
	}

	public static List<String> frenchMessages(ValidationRuntimeException e) {
		return frenchMessages(e.getValidationErrors());
	}

	public static List<String> frenchMessages(ValidationException e) {
		return frenchMessages(e.getErrors());

	}

	public static List<String> frenchMessages(com.constellio.model.frameworks.validation.ValidationException e) {
		return frenchMessages(e.getValidationErrors());

	}

	public static List<String> frenchMessages(ValidationErrors errors) {
		List<String> messages = new ArrayList<>();

		Locale originalLocale = i18n.getLocale();
		i18n.setLocale(Locale.FRENCH);

		for (ValidationError error : errors.getValidationErrors()) {
			messages.add($(error));
		}

		for (ValidationError error : errors.getValidationWarnings()) {
			messages.add($(error));
		}

		i18n.setLocale(originalLocale);

		return messages;
	}

	public static String frenchMessage(String key, Map<String, Object> args) {
		Locale originalLocale = i18n.getLocale();
		i18n.setLocale(Locale.FRENCH);
		String value = $(key, args);
		i18n.setLocale(originalLocale);
		return value;
	}

	public static String englishMessage(String key, Map<String, Object> args) {
		Locale originalLocale = i18n.getLocale();
		i18n.setLocale(Locale.ENGLISH);
		String value = $(key, args);
		i18n.setLocale(originalLocale);
		return value;
	}

	public static List<String> englishMessages(ValidationErrors errors) {
		List<String> messages = new ArrayList<>();

		Locale originalLocale = i18n.getLocale();
		i18n.setLocale(Locale.ENGLISH);

		for (ValidationError error : errors.getValidationErrors()) {
			messages.add($(error));
		}

		i18n.setLocale(originalLocale);

		return messages;
	}
}
