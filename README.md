# edu-openehr-templates

openEHR templates in software: examples for CaboLabs' training courses

## Build

```shell
$ gradle build
```

## Run from Gradle

NOTE: when running with gradle, the current working directory is ./app not ./ so the path is relative to ./app


### Try parse a template (returns an error if the OPT is not correct)

```shell
$ gradle run --args="parse src/main/resources/opts/test_all_types_en_v1.opt"
```


### Show internal structure of template

```shell
$ gradle run --args="traverse src/main/resources/opts/test_all_types_en_v1.opt"
```


### Show constraint at a template path

```shell
$ gradle run --args="constraint ../../openEHR-OPT/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-SECTION.test_all_types.v1]/items[at0001]/items[at0002]/items[archetype_id=openEHR-EHR-ACTION.test_all_types.v1]/ism_transition/careflow_step/defining_code"
```

```shell
$ gradle run --args="constraint ../../openEHR-OPT/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-OBSERVATION.test_all_types.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0013]/value"
```

```shell
$ gradle run --args="constraint src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-EVALUATION.test_all_types.v1]/data[at0001]/items[at0009]/value"
```


### Data validation

NOTE 1: for data validation the given path should correspond to a data value, usualy a path that constraints an ELEMENT.value attribute in the archetype.

NOTE 2: since a data set should be given, and each data value has different attributes, each data valu has it's own format for providing data:

- DV_QUANTITY: `magnitude|units` (magnitude is a number, units is a string)
- DV_ORDINAL: `value` or `terminology::code` or `value|terminology::code` (code is a string, value is a number, terminology is a string)
- CODE_PHRASE: `terminology::code` (code and terminology are both strings)
- DV_TEXT: `text` (text is a string, should be quoted if it has spaces)

NOTE 3: a template path could point to a node that has alternative data types, so the input data type should have the format of the type that is tried to be matched.

```shell
$ gradle run --args="validate ../../openEHR-OPT/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-OBSERVATION.test_all_types.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value 1000|mg"
```


## Build fat jar

```shell
$ gradle fatJar
```


### Parse template

```shell
$ java -jar app/build/libs/edu-openehr-templates-all.jar parse app/src/main/resources/opts/demographics.opt
```


### Show internal structure of template

```shell
$ java -jar app/build/libs/edu-openehr-templates-all.jar traverse app/src/main/resources/opts/demographics.opt 
```


### Show constraint at a template path

```shell
$ java -jar app/build/libs/edu-openehr-templates-all.jar constraint app/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-OBSERVATION.test_all_types.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value
```

```shell
$ java -jar app/build/libs/edu-openehr-templates-all.jar constraint app/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-EVALUATION.test_all_types.v1]/data[at0001]/items[at0009]/value
```


### Data validation

```shell
$ java -jar app/build/libs/edu-openehr-templates-all.jar validate app/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-OBSERVATION.test_all_types.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value "1000|mg"
```

```shell
$ java -jar app/build/libs/edu-openehr-templates-all.jar validate app/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-EVALUATION.test_all_types.v1]/data[at0001]/items[at0009]/value "120|mm[Hg]"
```