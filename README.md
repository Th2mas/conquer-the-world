# conquer-the-world

## Introduction 
conquer-the-world is a simple strategy game. The goal of this game is, as the name indicates, to conquer the world. As soon as a player has conquered all territories, the game is finished.

## Phases
Conquer the world consists of two phases: The acquiring and the conquering phase.

###Acquiring phase
Every player chooses alternately one unoccupied territory by clicking on it. (TODO: Add a picture)
As soon as all territories are occupied, the next conquering phase starts.

###Conquering phase
This phase consists of three sub-phases. The game will loop through those sub-phases, as soon as the acquisition phase has ended.
####Distribute reinforcements

####Attack and move
By clicking on a territory, which belongs to the player, and dragging it into a country, the player either attacks an opposing country or moves his/her armies to one of his nearby territories.
Is a player attacking a country, the player can attack at most with 3 armies. The defending player can defend at most with 2 armies. The attacking and defending is simulated by a virtual dice. TODO Add description 

####End round
As soon as a player has finished his/her round, the next player starts his/her round.
The player indicates this through clicking on the 'End round' button.

## Rules

### Basic Rules
The classic world map has 42 territories and 6 continents.

Every player starts with the same amount of territories. The sum of territories divided by 3 results in the amount of armies you get.
So for example if a player has 15 territories, the player will get 5 armies. For each conquered continent, the player gets additional armies. 
The number of armies, the player will get additionally are described in the section [Continent Bonus](#continent-bonus) 

### Boni
There are some boni a player can get during the game.

####Continent bonus
For each conquered continent, the player gets more reinforcements next round. 
For the classic world map, the number of reinforcements you can get per continent, are:
 * North America: 5
 * South America: 2
 * Europe: 5
 * Asia: 7
 * Africa: 3
 * Australia: 2
