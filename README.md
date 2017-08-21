# csvconvert

This program 'csvtransform' transforms a given csv file to another by userdefined rules. The rules are defined in a configuration file and can specify actions for columns like rename, re-ordering, delete and add.

The program takes three arguments (in the following order)

```
java -jar csvtransform.jar input.csv output.csv config.txt
```

1. **InputFile**: A csv file containing all the data that should be transformed. This file will remain the same after execution.
2. **OutputFile**: this file (is created if not exists) will be overwritten by the program. All the transformed data will be placed here after execution.
3. **ConfigFile**: A file with a special syntax that defines all the transformation rules.

The syntax for defining the Transformation works as follows:

```
originColumn > destinationColumn
```

Any column of the InputFile (here: originColumn) is renamed to destinationColumn.
The order of the rules in the configFile is adopted in the outputFile. This is how column re-ordering can be accomplished.
By omitting an originColumn in the configFile a deletion can be done. Only the listed columns will be carries into the outputFile.
For adding new columns in the outputFile leave the originColumn field blank. A default value can be given (by the colon-syntax) that will be filled in each row of the ouputFile. See the following syntax for an example.

```
 > NewColumn:defaultValue
```

If you want the '>' sign in a column header, you can escape it with ". The " character can be escapes by doubling it.
