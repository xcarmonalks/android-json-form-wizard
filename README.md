[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20Json%20Wizard-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1848)


Android Json Wizard
=========

Android Json Wizard is a library for creating beautiful form based wizards within your app just by defining json in a particular format.

# Demo
[![alt demo](http://img.youtube.com/vi/0PQD8EA8lEI/hqdefault.jpg)](https://www.youtube.com/watch?v=0PQD8EA8lEI)

[Demo Youtube Link](http://youtu.be/0PQD8EA8lEI)

# Usage

## Json Structure

Form json should consist of steps and fields.

## Steps

Step directly corresponds to a fragment(or a page) in wizard. It consists of different fields(array of fields), title and next step.

```json
    {
     "step1":{
             "fields":[
                 {
                     "key":"name",
                     "type":"edit_text",
                     "hint":"Enter Your Name"
                 },
                 {
                     "key":"email",
                     "type":"edit_text",
                     "hint":"Enter email address"
                 },
                 {
                     "key":"labelBackgroundImage",
                     "type":"label",
                     "text":"Choose Background Image"
                 },
                 {
                     "key":"chooseImage",
                     "type":"choose_image",
                     "uploadButtonText":"Choose"
                 }
             ],
             "title":"Step 1",
             "next":"step2"
         }
    }
```

## Supported fields

#### EditText
```json
    {
        "key":"name",
        "type":"edit_text",
        "hint":"Enter Your Name"
    }
```

key - must be unique in that particular step.

type - must be edit_text for EditText.

hint - hint for EditText.

lines - number of lines shown by the EditText

value - will be the value present in the editText after completion of wizard

##### EditText Required Validation

```json
"v_required" : {
                    "value" : "true",
                    "err" : "Please enter some value this is a required field."
               }
```

##### EditText Min length Validation

```json
"v_min_length" : {
                    "value" : "3",
                    "err" : "Min length should be at least 3"
                }
```

##### EditText Max Length Validation

```json
"v_max_length" : {
                    "value" : "30",
                    "err" : "Max length is 30"
                }
```

##### EditText Email Validation

```json
"v_email" : {
                    "value" : "true",
                    "err" : "Not an email."
                }
```

##### EditText Regex Validation

```json
"v_email" : {
                    "value" : "your-regex-here",
                    "err" : "Your message here."
                }
```

#### Label
```json
    {
     "key":"labelHeaderImage",
     "type":"label",
     "text":"Choose Background Image"
    }
```
key - must be unique in that particular step.

type - must be label for Label.

text - text for Label.


#### ImagePicker
```json
    {
     "key":"chooseImage",
     "type":"choose_image",
     "uploadButtonText":"Choose"
    }
```
key - must be unique in that particular step.

type - must be choose_image for ImagePicker.

uploadButtonText - text for Button of ImagePicker.

value - will be the path of chosen image on external storage

##### ImagePicker Required Validation

```json
"v_required" : {
                    "value" : "true",
                    "err" : "Please enter some value this is a required field."
               }
```

#### CheckBox (can be used for single/multiple CheckBoxes)
```json
    {
        "key":"checkData",
        "type":"check_box",
        "label":"Select multiple preferences",
        "options":[
            {
                "key":"awesomeness",
                "text":"Are you willing for some awesomeness?",
                "value":"false"
            },
            {
                "key":"newsletter",
                "text":"Do you really want to opt out from my newsletter?",
                "value":"false"
            }
        ]
    }

```

key - must be unique in that particular step.

type - must be check_box for CheckBox.

label - text for header of CheckBox.

options - options for CheckBox.

key(in options) - must be unique in options.

text(in options) - text fot the CheckBox.

value(in options) - true/false.

##### CheckBox Required Validation

Not supported yet.


#### Spinner
```json
        {
            "key":"name",
            "type":"spinner",
            "hint":"Name Thy House"
            "values":["Stark", "Targeriyan", "Lannister"]
        }
```

key - must be unique in that particular step.

type - must be spinner

hint - hint for Spinner.

values - Array of Strings.

value - will be the value present in the spinner after completion of wizard

##### Spinner Required Validation

```json
"v_required" : {
                    "value" : "true",
                    "err" : "Please enter some value this is a required field."
               }
```

#### RadioButton (can be used for single/multiple RadioButtons)

```json
{
    "key":"radioData",
    "type":"radio",
    "label":"Select one item from below",
    "options":[
        {
            "key":"areYouPro",
            "text":"Are you pro?"
        },
        {
            "key":"areYouAmature",
            "text":"Are you amature?"
        },
        {
            "key":"areYouNovice",
            "text":"Are you novice?"
        }
    ],
    "value":"areYouNovice"
}
```
key - must be unique in that particular step.

type - must be radio for RadioButton.

label - text for header of RadioButton.

value - must be key of one of the options which is selected/ or empty if no option is selected.

options - options for RadioButton.

key(in options) - must be unique in options.

text(in options) - text fot the RadioButton.

##### RadioButton Required Validation

Not supported yet.

#### DatePicker
```json
{
     "key":"selectDate",
     "type":"date_picker",
     "pattern":"dd/MM/yyyy",
     "hint":"Enter date"
}
```
key - must be unique in that particular step.

type - must be date_picker for DatePicker.

hint - hint for DatePicker.

pattern - Pattern used to format the selected date

value - will be the value present in the date picker after completion of wizard

#### EditGroup
```json
{
    "key":"editGroup",
    "type":"edit_group",
    "optNumber":"3",
    "title":"Group title",
    "fields":[
        {
            "key":"editText1",
            "type":"edit_text",
            "hint":"Enter value 1"
        },
        {
            "key":"editText2",
            "type":"edit_text",
            "hint":"Enter value 2"
        },
        {
            "key":"editText3",
            "type":"edit_text",
            "hint":"Enter value 3"
        }
    ]
}
```

key - must be unique in that particular step.

type - must be edit_group for EditGroup.

label - text for header of EditGroup (optional).

optNumber - number of childs of the group that will be processed

options - EditText included in the EditGroup.

#### Separator
```json
{
    "type":"separator"
}

```

#### Barcode EditText
```json
    {
        "key":"name",
        "type":"barcode_text",
        "hint":"Enter Your Name"
    }
```

key - must be unique in that particular step.

type - must be barcode_text for Barcode EditText.

hint - hint for Barcode EditText.

lines - number of lines shown by the Barcode EditText

value - will be the value present in the Barcode EditText after completion of wizard

##### Barcode EditText Required Validation
same as EditText

#### Resource Viewer

This widget will open HTML files in a WebView in a new Activity,
or attempt to launch an intent with the provided resource file.

```json
    {
        "key": "help",
        "type": "resource_view",
        "label": "Additional info",
        "resource": "docs/sample.pdf",
        "icon": "images/pdf.png",
        "config": {
            "color": "#0645AD",
            "size": 24,
            "align": "end",
            "icon_width": 54,
            "icon_height": 54,
            "icon_position": "end",
            "icon_color": "#0645AD"
        }
    }
```

key - any unique value, not really used since no value is read.

type - must be resource_view for this widget.

label - label to show in the form.

icon (optional) - icon resource to show.

resource - filename or URL of the resource to open.
    It will attempt to be resolved via ResourceResolver

config (optional) - widget configuration.

* Supports `color` (android hex string), `size` (in sp) and `align` ('start', 'center' or 'end').
* Also supports the following icon config: `icon_width` and `icon_height` (in dp),
`icon_position` ('start', 'end', 'bottom' or 'top'), defaulting to top,
and `icon_color` (android hex string).

* **Note: `icon_color` only works if the icon has a proper alpha channel.**

##### Custom intent support

It may also support launching a custom intent, given a custom intent serialized as follows:

`intent://<package>/<action>?<extra_name>=<extra_val>`

Note, however, that extra values are limited to String values.

e.g.

```
{
    "key": "help",
    "type": "resource_view",
    "label": "Additional info",
    "resource": "intent://com.google.android.apps.maps/android.intent.action.VIEW",
}
```

#### I18n
##### Bundle definition
```json
"bundle": {
  "es": {
    "step1.name": "Introduzca su nombre",
    "step1.radioData": "Seleccione un elemento"
  },
  "en": {
    "step1.name": "Enter Your Name",
    "step1.radioData": "Select one item from below"
  }
}
```

##### Using bundle labels
```json
{
    "key": "name",
    "type": "edit_text",
    "hint": "${step1.name}",
    "value":"name_val"
}

```

## Demo Input Json (Complete)

```json
{
    "count":"3",
    "step1":{
        "fields":[
            {
                "key":"name",
                "type":"edit_text",
                "hint":"Enter Your Name",
                "v_min_length":{  "value" : "3",
                                    "err" : "Min length should be at least 3"
                                },
                "v_max_length":{  "value" : "10",
                    "err" : "Max length can be at most 10."
                }
            },
            {
                "key":"email",
                "type":"edit_text",
                "hint":"Enter Your Email",
                "v_email":{  "value" : "true",
                    "err" : "Not an email."
                }
            },
            {
                 "key":"date",
                 "type":"date_picker",
                 "pattern":"dd/MM/yyyy",
                 "hint":"Enter date"
            },
            {
                "key":"labelBackgroundImage",
                "type":"label",
                "text":"Choose Background Image"
            },
            {
                "key":"chooseImage",
                "type":"choose_image",
                "uploadButtonText":"Choose",
                "v_required":{  "value" : "true",
                    "err" : "Please choose an image to proceed."
                }
            },
            {
                "key":"house",
                "type":"spinner",
                "hint": "Name Thy House",
                "values":["Stark", "Targeriyan", "Lannister"],
                "v_required":{  "value" : "true",
                    "err" : "Please choose a value to proceed."
                }
            }
        ],
        "title":"Step 1 of 3",
        "next":"step2"
    },
    "step2":{
        "fields":[
            {
                "key":"name",
                "type":"edit_text",
                "hint":"Enter Country"
            },
            {
                "key":"checkData",
                "type":"check_box",
                "label":"Select multiple preferences",
                "options":[
                    {
                        "key":"awesomeness",
                        "text":"Are you willing for some awesomeness?",
                        "value":"false"
                    },
                    {
                        "key":"newsletter",
                        "text":"Do you really want to opt out from my newsletter?",
                        "value":"false"
                    }
                ]
            },
            {
                "key":"radioData",
                "type":"radio",
                "label":"Select one item from below",
                "options":[
                    {
                        "key":"areYouPro",
                        "text":"Are you pro?"
                    },
                    {
                        "key":"areYouAmature",
                        "text":"Are you amature?"
                    },
                    {
                        "key":"areYouNovice",
                        "text":"Are you novice?"
                    }
                ],
                "value":"areYouNovice"
            }
        ],
        "title":"Step 2 of 3",
        "next":"step3"
    },
    "step3":{
        "fields":[
            {
                "key":"anything",
                "type":"edit_text",
                "hint":"Enter Anything You Want"
            }
        ],
        "title":"Step 3 of 3"
    },
    "bundle": {
       "es": {
         "step1.name": "Introduzca su nombre",
         "step1.radioData": "Seleccione un elemento"
       },
       "en": {
         "step1.name": "Enter Your Name",
         "step1.radioData": "Select one item from below"
       }
    }
}
```

## Starting form activity with your json

```java
    Intent intent = new Intent(context, JsonFormActivity.class);
    String json = "Your complete JSON";
    intent.putExtra("json", json);
    //Optional -- Configure screen orientation, inputMethod and visualizationMode
    intent.putExtra(JsonFormConstants.ORIENTATION_EXTRA, JsonFormConstants.ORIENTATION_LANDSCAPE);
    intent.putExtra(JsonFormConstants.INPUT_METHOD_EXTRA, JsonFormConstants.INPUT_METHOD_HIDDEN);
    intent.putExtra(JsonFormConstants.VISUALIZATION_MODE_EXTRA, JsonFormConstants.VISUALIZATION_MODE_READ_ONLY);
    startActivityForResult(intent, REQUEST_CODE_GET_JSON);
```

And receive result populated json in onActivityResult()

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
        Log.d(TAG, data.getStringExtra("json"));
    }
    super.onActivityResult(requestCode, resultCode, data);
}
```

### Starting form activity with a big json

Android Intent extras have a size limit; if the json is bigger than said limit,
you should send the data via provided `StateProvider`.

```java
    Intent intent = new Intent(context, JsonFormActivity.class);
    Uri jsonFileUri = StateProvider.saveState(context, json);
    intent.putExtra("jsonUri", jsonFileUri);
    startActivityForResult(intent, REQUEST_CODE_GET_JSON);
```

The same goes when receiving the result for a big json

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
        String json = data.getStringExtra("json");
        if (json == null) {
            Uri jsonUri = data.getParcelableExtra("uri");
            try (Cursor c = getContentResolver().query(jsonUri, null, null, null, null)) {
                if (c != null && c.moveToFirst()) {
                    json = c.getString(c.getColumnIndex("JSON"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Could not resolve JsonForm URI: " + jsonUri, e);
            }
        }
    }
    super.onActivityResult(requestCode, resultCode, data);
}
```

### History management

In some complex forms, it may be interesting to keep track of how the form was filled in each step.

In this case, when launching the form, set the extra value `trackHistory`
(you may use the constant in JsonFormConstants) to true.

```java
    Intent intent = new Intent(context, JsonFormActivity.class);
    String json = "Your complete JSON";
    intent.putExtra("json", json);
    //Optional -- Configure screen orientation, inputMethod and visualizationMode
    intent.putExtra(JsonFormConstants.EXTRA_TRACK_HISTORY, true);
    startActivityForResult(intent, REQUEST_CODE_GET_JSON);
```

Launching the form with history tracking enabled will make the output json
have an additional `_history` property with an array of every step and its state on exit.

Sample output json with `trackHistory` enabled:

```json
{
    "count":"3",
    "step1":{
        // -- snip
    },
    "step2":{
        // -- snip
    },
    "step3":{
        // -- snip
    },
    "_history": [
        {
            "name": "step1",
            "state": [
                 {
                     "key":"name",
                     "type":"edit_text",
                     "hint":"Enter Your Name",
                     "value":"Vijay"
                 },
                 {
                     "key":"email",
                     "type":"edit_text",
                     "hint":"Enter Your Email",
                     "value":"dummy@gmail.com"
                 },
                 {
                     "key":"labelBackgroundImage",
                     "type":"label",
                     "text":"Choose Background Image"
                 },
                 {
                     "key":"chooseImage",
                     "type":"choose_image",
                     "uploadButtonText":"Choose",
                     "value":"\/storage\/emulated\/0\/Pictures\/Wally\/10017.png"
                 }
            ]
        },
        {
            "name": "step2",
            "state": [
                 {
                     "key":"name",
                     "type":"edit_text",
                     "hint":"Enter Country",
                     "value":"India"
                 },
                 {
                     "key":"checkData",
                     "type":"check_box",
                     "label":"Select multiple preferences",
                     "options":[
                         {
                             "key":"awesomeness",
                             "text":"Are you willing for some awesomeness?",
                             "value":"true"
                         },
                         {
                             "key":"newsletter",
                             "text":"Do you really want to opt out from my newsletter?",
                             "value":"false"
                         }
                     ]
                 },
                 {
                     "key":"radioData",
                     "type":"radio",
                     "label":"Select one item from below",
                     "options":[
                         {
                             "key":"areYouPro",
                             "text":"Are you pro?"
                         },
                         {
                             "key":"areYouAmature",
                             "text":"Are you amature?"
                         },
                         {
                             "key":"areYouNovice",
                             "text":"Are you novice?"
                         }
                     ],
                     "value":"areYouPro"
                 }
            ]
        },
        {
            "name": "step3",
            "state": [
                 {
                     "key":"anything",
                     "type":"edit_text",
                     "hint":"Enter Anything You Want",
                     "value":"anything"
                 }
            ]
        }
    ],
}
```

## Output Json (of demo input json)

```json
{
    "count":"3",
    "step1":{
        "fields":[
            {
                "key":"name",
                "type":"edit_text",
                "hint":"Enter Your Name",
                "value":"Vijay"
            },
            {
                "key":"email",
                "type":"edit_text",
                "hint":"Enter Your Email",
                "value":"dummy@gmail.com"
            },
            {
                "key":"labelBackgroundImage",
                "type":"label",
                "text":"Choose Background Image"
            },
            {
                "key":"chooseImage",
                "type":"choose_image",
                "uploadButtonText":"Choose",
                "value":"\/storage\/emulated\/0\/Pictures\/Wally\/10017.png"
            }
        ],
        "title":"Step 1 of 3",
        "next":"step2"
    },
    "step2":{
        "fields":[
            {
                "key":"name",
                "type":"edit_text",
                "hint":"Enter Country",
                "value":"India"
            },
            {
                "key":"checkData",
                "type":"check_box",
                "label":"Select multiple preferences",
                "options":[
                    {
                        "key":"awesomeness",
                        "text":"Are you willing for some awesomeness?",
                        "value":"true"
                    },
                    {
                        "key":"newsletter",
                        "text":"Do you really want to opt out from my newsletter?",
                        "value":"false"
                    }
                ]
            },
            {
                "key":"radioData",
                "type":"radio",
                "label":"Select one item from below",
                "options":[
                    {
                        "key":"areYouPro",
                        "text":"Are you pro?"
                    },
                    {
                        "key":"areYouAmature",
                        "text":"Are you amature?"
                    },
                    {
                        "key":"areYouNovice",
                        "text":"Are you novice?"
                    }
                ],
                "value":"areYouPro"
            }
        ],
        "title":"Step 2 of 3",
        "next":"step3"
    },
    "step3":{
        "fields":[
            {
                "key":"anything",
                "type":"edit_text",
                "hint":"Enter Anything You Want",
                "value":"anything"
            }
        ],
        "title":"Step 3 of 3"
    }
}
```

### Obtaining partially completed form

Even if the form has not been finished, the partially completed data can be obtained
with a receiver listening for event `jsonFormPaused`.

```java
    public static final String FORM_PAUSED_ACTION = "jsonFormPaused";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FORM_PAUSED_ACTION.equals(intent.getAction())) {
            String json = intent.getStringExtra("json"); // Current form state
            String pausedStep = intent.getStringExtra("pausedStep"); // Name of the step where it was paused

            // If the form state is too big, the data will be provided via content URI
            if (json == null) {
                ContentResolver contentResolver = context.getContentResolver();
                String type = contentResolver.getType(uri);
                String jsonCol;
                if (type == null) {
                    throw new IllegalArgumentException("Unhandled URI: Undefined content type");
                }
                if (StateContract.ITEM_TYPE.equals(type)) {
                    jsonCol = StateContract.COL_JSON;
                } else {
                    throw new IllegalArgumentException("Unhandled URI: Unrecognized content type: " + type);
                }
                try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(jsonCol));
                    } else {
                        throw new IllegalStateException("Invalid URI: No data");
                    }
                }
            }
            // Work with the data...
        }
    }
```

# _WIP_ JsonPath support

Some widgets support the usage of JsonPath expressions to show or condition values according to "current" form values, e.g.

Conditionally compute whether to show next step:
```json
"next": {
    "step2": "$.current-values[?(@.age=='12-19')]"
}
```

Initialize a field value according to another value selected in a previous step:
```json
{
    "key": "name2",
    "type": "label",
    "text": "$.current-values.name"
}
```

Or load values from an external json, e.g.

`assets/data-library.json`
```json
{
    "books": [
        {
            "title": "Introduction to Programming",
            "isbn": "95-9361-770-1"
        },
        {
            "title": "Android for Dummies",
            "isbn": "99-9050-667-1"
        },
        {
            "title": "Machine Learning 101",
            "isbn": "95-0168-964-6"
        }
    ]
}
```

`form.json`
```json
{
    "count": 1,
    "step-1": {
        "fields": [
            {
                "key": "book",
                "type": "spinner",
                "hint": "Choose a book",
                "values": "@.data-library/books[*].isbn",
                "labels": "@.data-library/books[*].title",
            }
        ]
    }
}
```

Currently, JsonPath expressions are computed for the following widgets/properties:
- Label
  - `text`: The expression should resolve to a string.
- ExtendedLabel
  - `text`: The expression should resolve to a string.
  - `params`: Each of the param supports an expression which should resolve to a string.
- EditText
  - `readonly`, `v_required.value`: Checks for expression 'truthiness'.
  - `value`: The expression should resolve to a single string.
- Spinner
  - `values`, `labels`: Should resolve to an array of strings.
- RadioButton
  - `options`: Should resolve to an array of strings.
  - `value`: The expression should resolve to a string.
- BarcodeText
  - `readonly`, `v_required.value`: Checks for expression 'truthiness'.
  - `value`: The expression should resolve to a single string.
- Location
  - `readonly`, `v_required.value`: Checks for expression 'truthiness'.
  - `map_config`: Should resolve to a valid map config object.
  - `value`: The expression should resolve to a valid location string.
- Carousel
  - `values`, `images`: Should resolve to a string array.
- CheckBox
  - `show`: Checks for 'truthiness'.
  - `value`: Checks for 'truthiness' to set the default checked state.
- TimePicker
  - `v_required.value`: Checks for expression 'truthiness'.
- ResourceViewer
  - `label`, `resource`: Should resolve to a single string value.
  - `config`: Should resolve to a valid config object.


# Including in your project

gradle:

Step 1. Add the JitPack repository to your build file

```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```

Step 2. Add the dependency in the form

```groovy
dependencies {
    compile 'com.github.IndabaConsultores:android-json-form-wizard:1.9.0'
}
```

Step3. Add Android Material dependency

```
dependencies{
 implementation 'com.google.android.material:material:1.4.0'
}
```
maven:

Step 1. Add the JitPack repository to your build file

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

Step 2. Add the dependency in the form

```xml
<dependency>
    <groupId>com.github.IndabaConsultores</groupId>
    <artifactId>android-json-form-wizard</artifactId>
    <version>1.9.0</version>
</dependency>
```

Step 3. Add Android Material dependency
```
<!-- 
https://mvnrepository.com/artifact/com.google.android.material/material -->
<dependency>
    <groupId>com.google.android.material</groupId>
    <artifactId>material</artifactId>
    <version>1.4.0</version>
    <scope>runtime</scope>
</dependency>
```

##### Barcode Editext usage:
Barcode uses the MLKit API from Firebase. In order to use it, the following steeps are needed.
Step 1. Apply google services plugin
```gradle
apply plugin: 'com.google.gms.google-services'
```
Step 2. Add camera permission
```xml
<uses-permission android:name="android.permission.CAMERA"/>
```
Step 3. Add the Firebase MLKit vision API key to the app
Step 4 (Optional). Add metadata to the app Manifest to download the barcode module as soon as the app is installed.
```xml
<meta-data
    android:name="com.google.firebase.ml.vision.DEPENDENCIES"
    android:value="barcode"/>
```

# TODOs

- Support validation for Checkbox and RadioButton.
- Improve image picker UI.

# Contributing
Contributions welcome via Github pull requests.

# Credits

- [Android Material Components] (https://github.com/material-components/material-components-android)
- [material](https://github.com/rey5137/material)
- [MaterialEditText](https://github.com/rengwuxian/MaterialEditText)
- [MaterialSpinner](https://github.com/ganfra/MaterialSpinner)

Thanks!

# License
This project is licensed under the MIT License. Please refer the [License.txt](https://github.com/vijayrawatsan/android-json-form-wizard/blob/master/License.txt) file.
