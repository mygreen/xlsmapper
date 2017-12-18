
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

1. Add dependency for Super Csv Annotation
    ```xml
    <dependency>
        <groupId>com.github.mygreen</groupId>
        <artifactId>xlsmapper</artifactId>
        <version>2.0</version>
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

# Documentation
http://mygreen.github.io/xlsmapper/sphinx/howtouse.html

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
 




