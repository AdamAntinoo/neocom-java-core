# Java Code Snippets
## Core
[GET THE LAST NAME OF CLASSPATH]
longName.substring (longName.lastIndexOf ('.'))

[COMPARE TWO FILES]
assertEquals("The files differ!", 
    FileUtils.readFileToString(file1, "utf-8"), 
    FileUtils.readFileToString(file2, "utf-8"));

[JACKSON PARSE ARRAY OF OBJECTS]
MyClass[] myObjects = mapper.readValue(json, MyClass[].class);
List<MyClass> myObjects = mapper.readValue(jsonInput, mapper.getTypeFactory().constructCollectionType(List.class, MyClass.class));

[FROM FILE]
Car car = objectMapper.readValue(new File("src/test/resources/json_car.json"), Car.class);

[JUNIT5 THROW AN EXCEPTION]
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new JobScheduler.Builder()
						.withCronScheduleGenerator( null )
						.build(),
				"Expected JobScheduler.Builder() to throw null verification, but it didn't." );

[USE TESTCONTAINERS FOR TESTING]
	@Rule
	public PostgreSQLContainer postgres = new PostgreSQLContainer( "postgres:9.6.8" )
			.withDatabaseName( "postgres" )
			.withUsername( "neocom" )
			.withPassword( "01.Alpha" );
