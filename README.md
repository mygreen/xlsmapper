[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mygreen/xlsmapper/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mygreen/xlsmapper/)
[![Javadocs](http://javadoc.io/badge/com.github.mygreen/xlsmapper.svg?color=blue)](http://javadoc.io/doc/com.github.mygreen/xlsmapper)
[![Build Status](https://travis-ci.org/mygreen/xlsmapper.svg?branch=master)](https://travis-ci.org/mygreen/xlsmapper)
[![SonarQube](https://sonarcloud.io/api/project_badges/measure?project=com.github.mygreen%3Axlsmapper&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.mygreen%3Axlsmapper)

# XlsMapper

XlsMapper is Java Library for mapping Excel sheets to POJO.

# Licensee

Apache License verion 2.0

# Depends
+ Java1.8
+ Apache POI v3.17
+ SpringFramework 3.0+ (optional)
+ BeanValidation 1.0/1.1/2.0 (optional)

# Setup

1. Add dependency for XlsMapper
    ```xml
    <dependency>
        <groupId>com.github.mygreen</groupId>
        <artifactId>xlsmapper</artifactId>
        <version>2.1</version>
    </dependency>
    ```

2. Add dependency for Logging library. Example Log4j.
    ```xml
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.1</version>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.14</version>
    </dependency>
    ```

# Build

1. Setup Java SE 8 (1.8.0_121+)
2. Setup Maven
3. Setup Sphinx (building for manual)
    1. install Python
    2. install sphinx and theme for read the docs, janome
    ```console
    # pip install sphinx
    # pip install sphinx_rtd_theme --upgrade
    # pip install janome
    ```
4. Build with Maven
    1. make jar files.
    ```console
    # mvn clean package
    ```
    2. generate site.
    ```console
    # mvn site -Dgpg.skip=true
    ```

# Documentation
- Project infomation
  - http://mygreen.github.io/xlsmapper/index.html
- Manual
  - http://mygreen.github.io/xlsmapper/sphinx/index.html
- Javadoc
  - http://mygreen.github.io/xlsmapper/apidocs/index.html
  - http://javadoc.io/doc/com.github.mygreen/xlsmapper/

# Getting Started
For example, here is one Excel sheet.

![Sample Excel](src/site/sphinx/source/_static/howto_load.png)

Map this Excel sheet to POJO.
- Map the sheet with annotation ```@XlsSheet``` .
- Map the cell 'Date' with annotation ```@XlsLabelledCell``` .
- Map the table list 'User List' with annotation ```@XlsHorizontalRecords``` .

```java
// POJO for mapping sheet.
@XlsSheet(name="List")
public class UserSheet {

    @XlsLabelledCell(label="Date", type=LabelledCellType.Right)
    Date createDate;

    @XlsHorizontalRecords(tableLabel="User List")
    List<UserRecord> users;

}
```

And the following is the record class.
- Properties of the record class is mapped to columns by ```@XlsColumn``` .
- Can map to int and enum type. 

```java
// Record class
public class UserRecord {

    @XlsColumn(columnName="ID")
    int no;

    @XlsColumn(columnName="Class", merged=true)
    String className;

    @XlsColumn(columnName="Name")
    String name;

    @XlsColumn(columnName="Gender")
    Gender gender;

}

// enum for the gender.
public enum Gender {
    male, female;
}
```

You can get the mapped POJO using XlsMapper#load() like following:

```java
// Load sheet with mapping to POJO.
XlsMapper xlsMapper = new XlsMapper();
UserSheet sheet = xlsMapper.load(
    new FileInputStream("example.xls"), // excel sheet.
    UserSheet.class                     // POJO class.
    );
```

## How to saving the sheet.

For example with saving the sheet, using same sheet.

Here is the template Excel sheet.

![Sample Excel](src/site/sphinx/source/_static/howto_save.png)


And the following is the record class. 
- Append the annotation ```@XlsDateTimeConverter``` for setting Excel format pattern.
- Append the annotation ```@XlsRecordOption``` and attribute ```overOperation``` .

```java
@XlsSheet(name="List")
public class UserSheet {

    @XlsLabelledCell(label="Date", type=LabelledCellType.Right)
    @XlsDateTimeConverter(excelPattern="yyyy/m/d")
    Date createDate;

    @XlsHorizontalRecords(tableLabel="User List")
    @XlsRecordOption(overOperation=OverOperation.Insert)
    List<UserRecord> users;

}
```

You can save the Excel with POJO using XlsMapper#save() like following:

```java
// Create sheet data.
UserSheet sheet = new UserSheet();
sheet.date = new Date();

List<UserRecord> users = new ArrayList<>();

// Create record data.
UserRecord record1 = new UserRecord();
record1.no = 1;
record1.className = "A";
record1.name = "Ichiro";
record1.gender = Gender.male;
users.add(record1);

UserRecord record2 = new UserRecord();
// ...
users.add(record2);

sheet.users = users;

// Save the Excel sheet.
XlsMapper xlsMapper = new XlsMapper();
xlsMapper.save(
    new FileInputStream("template.xls"), // for template excel file.
    new FileOutputStream("out.xls"),     // for output excel file.
    sheet                                // for created sheet data.
    );
```
 




