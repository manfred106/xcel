# xcel

Java libraries for validating Excel-related workflows.

## Modules
- `xcel-validation-core` – core validation APIs and rules
- `xcel-validation-poi` – Apache POI-based implementations

## JSON configuration examples
Each field maps to a header and a list of validators.

### Required
```json
{
  "fields": [
    { "fieldName": "Email", "required": true }
  ]
}
```

### Data type
```json
{
  "fields": [
    { "fieldName": "Count", "dataType": "Integer" }
  ]
}
```

### Regex
```json
{
  "fields": [
    { "fieldName": "Code", "validators": [ { "type": "regex", "options": { "pattern": "^[A-Z]{3}$" } } ] }
  ]
}
```

### Range
```json
{
  "fields": [
    { "fieldName": "Age", "validators": [ { "type": "range", "options": { "min": 10, "max": 150 } } ] }
  ]
}
```

### Length
```json
{
  "fields": [
    { "fieldName": "Name", "validators": [ { "type": "length", "options": { "min": 3, "max": 50 } } ] }
  ]
}
```

### DateTime
```json
{
  "fields": [
    { "fieldName": "Date", "validators": [ { "type": "dateTime", "options": { "format": "yyyy-MM-dd" } } ] }
  ]
}
```

### Set
```json
{
  "fields": [
    { "fieldName": "Status", "validators": [ { "type": "set", "options": { "values": ["NEW", "DONE"] } } ] }
  ]
}
```

### Unique
```json
{
  "fields": [
    { "fieldName": "Email", "validators": [ { "type": "unique" } ] }
  ]
}
```

### Conditional (multi-field)
```json
{
  "fields": [
    {
      "fieldName": "Discount",
      "validators": [
        {
          "type": "conditional",
          "options": {
            "mode": "all",
            "conditions": [
              { "type": "dataType", "options": { "dataType": "Integer", "fieldName": "Age" } },
              { "type": "set", "options": { "values": ["VIP"], "fieldName": "Status" } }
            ],
            "validators": [
              { "type": "range", "options": { "min": 1, "max": 10 } }
            ]
          }
        }
      ]
    }
  ]
}
```

### Combined example
```json
{
  "fields": [
    {
      "fieldName": "Age",
      "required": true,
      "dataType": "Integer",
      "validators": [
        { "type": "range", "options": { "min": 1, "max": 150 } }
      ]
    }
  ]
}
```

## Requirements
- Java 17+
- Gradle (wrapper included)

## Build
```
./gradlew build
```

## Tests
```
./gradlew test
```

## Validation sessions
For validators that keep per-run state (e.g., `unique`), the core uses a validation session
to avoid shared state across runs.

Core usage:
```java
ValidationSession session = new CoreValidator().startSession(config);
List<ValidationError> errors = session.validateRow(rowMap, rowIndex);
```

## Contributing
See `CONTRIBUTING.md`.

## License
Apache License 2.0. See `LICENSE`.
