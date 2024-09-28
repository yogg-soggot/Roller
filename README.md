### Simple dice rolling bot for tabletop rpgs

#### Abilities:

- /roller - creates message with 5 buttons (+1 to +5) and roll history. Clicking on +n button will roll D10+n dice
- /extended - roller with more buttons (-2 to +7)
- /difficulty - sets global difficulty k for the whole guild. All rolls will be rolled as D10+n-k

##### Run self-hosted instance:
- Create discord bot on https://discord.com/developers/applications
- Get bot token
- Download jar from releases
- Run java -jar Ino.jar -{token}