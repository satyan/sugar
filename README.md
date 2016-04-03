# Sugar ORM [![Build Status](https://travis-ci.org/satyan/sugar.svg?branch=master)](https://travis-ci.org/satyan/sugar) [![Coverage Status](https://coveralls.io/repos/satyan/sugar/badge.svg?branch=master)](https://coveralls.io/r/satyan/sugar?branch=master) [![Code Triagers Badge](http://www.codetriage.com/satyan/sugar/badges/users.svg)](http://www.codetriage.com/satyan/sugar)

[![Join the chat at https://gitter.im/satyan/sugar](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/satyan/sugar?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Insanely easy way to work with Android databases.

Official documentation can be found [here](http://satyan.github.io/sugar) - Check some examples below. The example application is provided in the **example** folder in the source.

## Features

Sugar ORM was built in contrast to other ORM's to have:

- A simple, concise, and clean integration process with minimal configuration.
- Automatic table and column naming through reflection.
- Support for migrations between different schema versions.

## Installing

There are four ways to install Sugar:

#### As a Gradle dependency

This is the preferred way. Simply add:

```groovy
compile 'com.github.satyan:sugar:1.5'
```

to your project dependencies and run `gradle build` or `gradle assemble`.

#### As a Maven dependency

Declare the dependency in Maven:

```xml
<dependency>
    <groupId>com.github.satyan</groupId>
    <artifactId>sugar</artifactId>
    <version>1.5</version>
</dependency>
```

#### As a library project

Download the source code and import it as a library project in Eclipse. The project is available in the folder **library**. For more information on how to do this, read [here](http://developer.android.com/tools/projects/index.html#LibraryProjects).

#### As a jar

Visit the [releases](https://github.com/satyan/sugar/releases) page to download jars directly. You can drop them into your `libs` folder and configure the Java build path to include the library. See this [tutorial](http://www.vogella.com/tutorials/AndroidLibraryProjects/article.html) for an excellent guide on how to do this.


### How to use master version
First, download sugar repository
```
git clone git@github.com:satyan/sugar.git
```

include this in your **settings.gradle**
```gradle
include ':app' // your module app
include ':sugar'

def getLocalProperty(prop) {
	Properties properties = new Properties()
	properties.load(new File(rootDir.absolutePath + '/local.properties').newDataInputStream())
	return properties.getProperty(prop, '')
}

project(':sugar').projectDir = new File(getLocalProperty('sugar.dir'))

```

include this in your **local.properties**
```
sugar.dir=/path/to/sugar/library
```

add sugar project to the dependencies of your main project (build.gradle)
```gradle
dependencies {
    compile project(':sugar')
}
```

===================

After installing, check out how to set up your first database and models [here](http://satyan.github.io/sugar/getting-started.html) **Outdated**. Check examples of 1.4 and master below: 

## Examples
### SugarRecord
```java
public class Book extends SugarRecord {
  @Unique
  String isbn;
  String title;
  String edition;

  // Default constructor is necessary for SugarRecord
  public Book() {

  }

  public Book(String isbn, String title, String edition) {
    this.isbn = isbn;
    this.title = title;
    this.edition = edition;
  }
}
```
or
```java
@Table
public class Book { ... }
```

### Save Entity
```java
Book book = new Book("isbn123", "Title here", "2nd edition")
book.save();
```

or
```java
SugarRecord.save(book); // if using the @Table annotation 
```

### Load Entity
```java
Book book = Book.findById(Book.class, 1);
```

### Update Entity
```java
Book book = Book.findById(Book.class, 1);
book.title = "updated title here"; // modify the values
book.edition = "3rd edition";
book.save(); // updates the previous entry with new values.
```


### Delete Entity
```java
Book book = Book.findById(Book.class, 1);
book.delete();
```

or
```java
SugarRecord.delete(book); // if using the @Table annotation 
```

### Update Entity based on Unique values
```java
Book book = new Book("isbn123", "Title here", "2nd edition")
book.save();

// Update book with isbn123
Book sameBook = new Book("isbn123", "New Title", "5th edition")
sameBook.update();

book.getId() == sameBook.getId(); // true
```

or
```java
SugarRecord.update(sameBook); // if using the @Table annotation 
```

### Bulk Insert
```java
List<Book> books = new ArrayList<>();
books.add(new Book("isbn123", "Title here", "2nd edition"))
books.add(new Book("isbn456", "Title here 2", "3nd edition"))
books.add(new Book("isbn789", "Title here 3", "4nd edition"))
SugarRecord.saveInTx(books);
```

# Using the ContentProvider for the CursorLoader

Content providers are vital when using CursorLoaders and SyncAdapters, this modification adds a ContentProvider to work with those systems.

###Modify the manifest

The names of the manifest meta properties have change in this version to avoid conflicts with other libraries.

```xml
<application ...>
	
<meta-data android:name="SUGAR_DATABASE" android:value="sugar.db"/>
<meta-data android:name="SUGAR_VERSION" android:value="114"/>
<meta-data android:name="SUGAR_QUERY_LOG" android:value="true"/>
<meta-data android:name="SUGAR_DOMAIN_PACKAGE_NAME" android:value="my.model.package"/>

<!-- Enable this for legacy compatibility with Sugar 1.5 or earlier. -->
<!-- <meta-data android:name="SUGAR_ID_COLUMN_NAME" android:value="id"/> -->

<!-- the name of the authority should be the same as the one use in the provider definition. -->
<meta-data android:name="SUGAR_AUTHORITY" android:value="my.content.authority"/> 
<!-- This changes the behaviour of the schema generator. If removed, the legacy behaviour will be used. -->
<!-- This version drops all tables and recreates them during a database version upgrade or downgrade.
	Most applicaitons that use sync adapters and/or store their data in the cloud, do not need the 
	complexity of migration. -->
<meta-data android:name="SUGAR_SCHEMA_HELPER_CLASS" android:value="com.orm.helper.DropTableSchemaGenerator"/>

<!-- the authirity listed for the content provider should match the one set inthe meta configuration tag. -->
<provider
	android:name="com.orm.SugarContentProvider"
	android:authorities="my.content.authority"
	android:exported="false"/>
	
</application>
```
### Setting up a CursorLoader

This version changes the default ID column name from "id" to the standard "_id" (as defined by android.provider.BaseColumns._ID).
The ID column name change is important because several built in components of SimpleCursorAdapter expect the _id column to have a specific name.

```java
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		// Note that these are column names, not pojo properties.
		String select = "col1 = ? AND col2 = ? AND col3 != ?";
		String[] selectArgs = {
				"value1",
				"value2",
				"value3"
		};
		String orderBy = "col1 DESC, col2 ASC";

		// Note that we currently don't need the projection, however this may not 
		// always be true since we don't need to load entire Pojo's into memory.
		String[] projection = null;

		final Uri uri = SugarContentProvider.createUri(CxOrganization.class, null);

		return new CursorLoader(context, uri, projection, null, null, orderBy);
	}
```

## Tips for using CursorLoader

When setting up your POJO with annotations, create an interface that contains 
the names of the columns for the annotations. This will allow you to refere to and refactor later.

```java
interface Columns {
	String NAME = "col_name";
	String ORDER = "col_order";
}

@Table("my_pojo")
public class MyPojo {
	@Column(name = Columns.NAME)
	private String name;
	
	@Column(name = Columns.ORDER)
	private int order;
}

```

Setting up the columns that way will ensure you don't get a typo and the column names can be used in quiery builders.

You can also use the column names when setting up your SimpleCursorAdapter.

```java
	// code fragment found when initializing a ListView.
	adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.my_list_item, null,
				new String[]{
						Columns.NAME,
						Columns.ORDER
				},
				new int[]{
						R.id.name,
						R.id.order
				}, 0);
```

# When using ProGuard
```java
# Ensures entities remain un-obfuscated so table and columns are named correctly
-keep class com.yourpackage.yourapp.domainclasspackage.** { *; }
```

## [CHANGELOG](https://github.com/satyan/sugar/blob/master/CHANGELOG.md)

## Contributing

Please fork this repository and contribute back using [pull requests](https://github.com/satyan/sugar/pulls). Features can be requested using [issues](https://github.com/satyan/sugar/issues). All code, comments, and critiques are greatly appreciated.
