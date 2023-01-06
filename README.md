# testlab2-krav

## Utvikling

### Formatering

All kotlinkode skal formateres med ktfmt og default stil. Det blir sjekket automatisk ved alle bygg med
[spotless-maven-plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven).

#### Formatering i IDEA

Installer plugin-en [ktfmt](https://plugins.jetbrains.com/plugin/14912-ktfmt) for å formatere med ktfmt i IDEA. Husk å
skru den på i settings etter at du har installert den.

Du kan også formatere automatisk når du lagrer ved å skru på `Settings -> Tools -> Actions on Save -> Reformat code`. 