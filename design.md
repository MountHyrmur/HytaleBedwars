# Hytale Bedwars Technical design doc

# Abstractions
## Generator id
Just a string, set on the generator entity, and then used to assign a generator configuration.

# UI / UX / DX
Generators and shopkeepers will be entities, which will be moveable using the built-in Hytale entity tool.

`/bedwars` command will be used to manage levels, and edit them
`/bedwars map create <name>`
`/bedwars map edit <name>`
`/bedwars map delete <name>`
`/bedwars map list`