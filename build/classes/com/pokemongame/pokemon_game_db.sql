-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 18, 2026 at 06:19 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pokemon_game_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `item_id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `type` enum('HEAL','RECOVER_PP','BALL','STAT') DEFAULT NULL,
  `effect_value` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`item_id`, `name`, `type`, `effect_value`) VALUES
(1, 'Potion', 'HEAL', 20),
(2, 'Super Potion', 'HEAL', 50),
(3, 'Hyper Potion', 'HEAL', 120),
(4, 'Poke Ball', 'BALL', 1),
(5, 'Great Ball', 'BALL', 2),
(6, 'Ultra Ball', 'BALL', 3),
(7, 'X Speed', 'STAT', 15);

-- --------------------------------------------------------

--
-- Table structure for table `moves_base`
--

CREATE TABLE `moves_base` (
  `move_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `power` int(11) NOT NULL,
  `accuracy` int(11) NOT NULL,
  `type` varchar(20) NOT NULL,
  `category` enum('PHYSICAL','SPECIAL') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `moves_base`
--

INSERT INTO `moves_base` (`move_id`, `name`, `power`, `accuracy`, `type`, `category`) VALUES
(1, 'Tackle', 40, 100, 'NORMAL', 'PHYSICAL'),
(2, 'Ember', 40, 100, 'FIRE', 'SPECIAL'),
(3, 'Water Gun', 40, 100, 'WATER', 'SPECIAL'),
(4, 'Vine Whip', 45, 100, 'GRASS', 'SPECIAL'),
(7, 'Flamethrower', 90, 100, 'FIRE', 'SPECIAL'),
(8, 'Thunderbolt', 90, 100, 'ELECTRIC', 'SPECIAL'),
(9, 'Surf', 90, 100, 'WATER', 'SPECIAL'),
(10, 'Earthquake', 100, 100, 'GROUND', 'PHYSICAL'),
(11, 'Ice Beam', 90, 100, 'ICE', 'SPECIAL'),
(12, 'Psychic', 90, 100, 'PSYCHIC', 'SPECIAL'),
(13, 'Solar Beam', 120, 100, 'GRASS', 'SPECIAL'),
(14, 'Dragon Claw', 80, 100, 'DRAGON', 'PHYSICAL'),
(15, 'Shadow Ball', 80, 100, 'GHOST', 'SPECIAL'),
(16, 'Hyper Beam', 150, 90, 'NORMAL', 'SPECIAL'),
(17, 'Sludge Bomb', 90, 100, 'POISON', 'SPECIAL'),
(18, 'Fly', 90, 95, 'FLYING', 'PHYSICAL'),
(19, 'Brick Break', 75, 100, 'FIGHTING', 'PHYSICAL'),
(20, 'Rock Slide', 75, 90, 'ROCK', 'PHYSICAL'),
(21, 'X-Scissor', 80, 100, 'BUG', 'PHYSICAL'),
(22, 'Iron Tail', 100, 75, 'STEEL', 'PHYSICAL'),
(23, 'Dark Pulse', 80, 100, 'DARK', 'SPECIAL'),
(24, 'Moonblast', 95, 100, 'FAIRY', 'SPECIAL');

-- --------------------------------------------------------

--
-- Table structure for table `player_inventory`
--

CREATE TABLE `player_inventory` (
  `player_id` int(11) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `player_inventory`
--

INSERT INTO `player_inventory` (`player_id`, `item_id`, `quantity`) VALUES
(1, 1, 10),
(1, 2, 5),
(1, 3, 2),
(1, 4, 20),
(1, 5, 8),
(1, 6, 3),
(1, 7, 5);

-- --------------------------------------------------------

--
-- Table structure for table `player_pokemon`
--

CREATE TABLE `player_pokemon` (
  `instance_id` int(11) NOT NULL,
  `player_id` int(11) DEFAULT NULL,
  `poke_id` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT 5,
  `current_hp` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `player_pokemon`
--

INSERT INTO `player_pokemon` (`instance_id`, `player_id`, `poke_id`, `level`, `current_hp`, `exp`) VALUES
(1, 1, 498, 5, 85, 0),
(2, 1, 495, 6, 67, 0),
(3, 1, 501, 5, 75, 0),
(4, 1, 620, 5, 85, 0),
(5, 1, 612, 7, 103, 0),
(6, 1, 543, 3, 46, 0),
(7, 1, 617, 5, 100, 0);

-- --------------------------------------------------------

--
-- Table structure for table `player_save`
--

CREATE TABLE `player_save` (
  `id` int(11) NOT NULL,
  `player_name` varchar(50) DEFAULT 'Trainer',
  `world_x` int(11) DEFAULT 3200,
  `world_y` int(11) DEFAULT 3200,
  `money` int(11) DEFAULT 1000,
  `last_saved` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `player_save`
--

INSERT INTO `player_save` (`id`, `player_name`, `world_x`, `world_y`, `money`, `last_saved`) VALUES
(1, 'Ash', 3200, 3200, 1000, '2026-05-14 14:31:32');

-- --------------------------------------------------------

--
-- Table structure for table `pokemon_base`
--

CREATE TABLE `pokemon_base` (
  `poke_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `type1` varchar(20) NOT NULL,
  `type2` varchar(20) DEFAULT NULL,
  `base_hp` int(11) NOT NULL,
  `base_atk` int(11) NOT NULL,
  `base_def` int(11) NOT NULL,
  `base_spd` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pokemon_base`
--

INSERT INTO `pokemon_base` (`poke_id`, `name`, `type1`, `type2`, `base_hp`, `base_atk`, `base_def`, `base_spd`) VALUES
(494, 'Victini', 'Psychic', 'Fire', 100, 100, 100, 100),
(495, 'Snivy', 'Grass', NULL, 45, 45, 55, 63),
(496, 'Servine', 'Grass', NULL, 60, 60, 75, 83),
(497, 'Serperior', 'Grass', NULL, 75, 75, 95, 113),
(498, 'Tepig', 'Fire', NULL, 65, 63, 45, 45),
(499, 'Pignite', 'Fire', 'Fighting', 90, 93, 55, 55),
(500, 'Emboar', 'Fire', 'Fighting', 110, 123, 65, 65),
(501, 'Oshawott', 'Water', NULL, 55, 55, 45, 45),
(502, 'Dewott', 'Water', NULL, 75, 75, 60, 60),
(503, 'Samurott', 'Water', NULL, 95, 100, 85, 70),
(504, 'Patrat', 'Normal', NULL, 45, 55, 39, 42),
(505, 'Watchog', 'Normal', NULL, 60, 85, 69, 77),
(506, 'Lillipup', 'Normal', NULL, 45, 60, 45, 55),
(507, 'Herdier', 'Normal', NULL, 65, 80, 65, 60),
(508, 'Stoutland', 'Normal', NULL, 85, 110, 90, 80),
(509, 'Purrloin', 'Dark', NULL, 41, 50, 37, 66),
(510, 'Liepard', 'Dark', NULL, 64, 88, 50, 106),
(511, 'Pansage', 'Grass', NULL, 50, 53, 48, 64),
(512, 'Simisage', 'Grass', NULL, 75, 98, 63, 101),
(513, 'Pansear', 'Fire', NULL, 50, 53, 48, 64),
(514, 'Simisear', 'Fire', NULL, 75, 98, 63, 101),
(515, 'Panpour', 'Water', NULL, 50, 53, 48, 64),
(516, 'Simipour', 'Water', NULL, 75, 98, 63, 101),
(517, 'Munna', 'Psychic', NULL, 76, 25, 45, 24),
(518, 'Musharna', 'Psychic', NULL, 116, 55, 85, 29),
(519, 'Pidove', 'Normal', 'Flying', 50, 55, 50, 43),
(520, 'Tranquill', 'Normal', 'Flying', 62, 77, 62, 65),
(521, 'Unfezant', 'Normal', 'Flying', 80, 115, 80, 93),
(522, 'Blitzle', 'Electric', NULL, 45, 60, 32, 76),
(523, 'Zebstrika', 'Electric', NULL, 75, 100, 63, 116),
(524, 'Roggenrola', 'Rock', NULL, 55, 75, 85, 15),
(525, 'Boldore', 'Rock', NULL, 70, 105, 105, 20),
(526, 'Gigalith', 'Rock', NULL, 85, 135, 130, 25),
(527, 'Woobat', 'Psychic', 'Flying', 65, 45, 43, 72),
(528, 'Swoobat', 'Psychic', 'Flying', 67, 57, 55, 114),
(529, 'Drilbur', 'Ground', NULL, 60, 85, 40, 68),
(530, 'Excadrill', 'Ground', 'Steel', 110, 135, 60, 88),
(531, 'Audino', 'Normal', NULL, 103, 60, 86, 50),
(532, 'Timburr', 'Fighting', NULL, 75, 80, 55, 35),
(533, 'Gurdurr', 'Fighting', NULL, 85, 105, 85, 40),
(534, 'Conkeldurr', 'Fighting', NULL, 105, 140, 95, 45),
(535, 'Tympole', 'Water', NULL, 50, 50, 40, 64),
(536, 'Palpitoad', 'Water', 'Ground', 75, 65, 55, 69),
(537, 'Seismitoad', 'Water', 'Ground', 105, 95, 75, 74),
(538, 'Throh', 'Fighting', NULL, 120, 100, 85, 45),
(539, 'Sawk', 'Fighting', NULL, 75, 125, 75, 85),
(540, 'Sewaddle', 'Bug', 'Grass', 45, 53, 70, 42),
(541, 'Swadloon', 'Bug', 'Grass', 55, 63, 90, 42),
(542, 'Leavanny', 'Bug', 'Grass', 75, 103, 80, 92),
(543, 'Venipede', 'Bug', 'Poison', 30, 45, 59, 57),
(544, 'Whirlipede', 'Bug', 'Poison', 40, 55, 99, 47),
(545, 'Scolipede', 'Bug', 'Poison', 60, 100, 89, 112),
(546, 'Cottonee', 'Grass', 'Fairy', 40, 27, 60, 66),
(547, 'Whimsicott', 'Grass', 'Fairy', 60, 67, 85, 116),
(548, 'Petilil', 'Grass', NULL, 45, 35, 50, 30),
(549, 'Lilligant', 'Grass', NULL, 70, 60, 75, 90),
(550, 'Basculin-red-striped', 'Water', NULL, 70, 92, 65, 98),
(551, 'Sandile', 'Ground', 'Dark', 50, 72, 35, 65),
(552, 'Krokorok', 'Ground', 'Dark', 60, 82, 45, 74),
(553, 'Krookodile', 'Ground', 'Dark', 95, 117, 80, 92),
(554, 'Darumaka', 'Fire', NULL, 70, 90, 45, 50),
(555, 'Darmanitan-standard', 'Fire', NULL, 105, 140, 55, 95),
(556, 'Maractus', 'Grass', NULL, 75, 86, 67, 60),
(557, 'Dwebble', 'Bug', 'Rock', 50, 65, 85, 55),
(558, 'Crustle', 'Bug', 'Rock', 70, 105, 125, 45),
(559, 'Scraggy', 'Dark', 'Fighting', 50, 75, 70, 48),
(560, 'Scrafty', 'Dark', 'Fighting', 65, 90, 115, 58),
(561, 'Sigilyph', 'Psychic', 'Flying', 72, 58, 80, 97),
(562, 'Yamask', 'Ghost', NULL, 38, 30, 85, 30),
(563, 'Cofagrigus', 'Ghost', NULL, 58, 50, 145, 30),
(564, 'Tirtouga', 'Water', 'Rock', 54, 78, 103, 22),
(565, 'Carracosta', 'Water', 'Rock', 74, 108, 133, 32),
(566, 'Archen', 'Rock', 'Flying', 55, 112, 45, 70),
(567, 'Archeops', 'Rock', 'Flying', 75, 140, 65, 110),
(568, 'Trubbish', 'Poison', NULL, 50, 50, 62, 65),
(569, 'Garbodor', 'Poison', NULL, 80, 95, 82, 75),
(570, 'Zorua', 'Dark', NULL, 40, 65, 40, 65),
(571, 'Zoroark', 'Dark', NULL, 60, 105, 60, 105),
(572, 'Minccino', 'Normal', NULL, 55, 50, 40, 75),
(573, 'Cinccino', 'Normal', NULL, 75, 95, 60, 115),
(574, 'Gothita', 'Psychic', NULL, 45, 30, 50, 45),
(575, 'Gothorita', 'Psychic', NULL, 60, 45, 70, 55),
(576, 'Gothitelle', 'Psychic', NULL, 70, 55, 95, 65),
(577, 'Solosis', 'Psychic', NULL, 45, 30, 40, 20),
(578, 'Duosion', 'Psychic', NULL, 65, 40, 50, 30),
(579, 'Reuniclus', 'Psychic', NULL, 110, 65, 75, 30),
(580, 'Ducklett', 'Water', 'Flying', 62, 44, 50, 55),
(581, 'Swanna', 'Water', 'Flying', 75, 87, 63, 98),
(582, 'Vanillite', 'Ice', NULL, 36, 50, 50, 44),
(583, 'Vanillish', 'Ice', NULL, 51, 65, 65, 59),
(584, 'Vanilluxe', 'Ice', NULL, 71, 95, 85, 79),
(585, 'Deerling', 'Normal', 'Grass', 60, 60, 50, 75),
(586, 'Sawsbuck', 'Normal', 'Grass', 80, 100, 70, 95),
(587, 'Emolga', 'Electric', 'Flying', 55, 75, 60, 103),
(588, 'Karrablast', 'Bug', NULL, 50, 75, 45, 60),
(589, 'Escavalier', 'Bug', 'Steel', 70, 135, 105, 20),
(590, 'Foongus', 'Grass', 'Poison', 69, 55, 45, 15),
(591, 'Amoonguss', 'Grass', 'Poison', 114, 85, 70, 30),
(592, 'Frillish-male', 'Water', 'Ghost', 55, 40, 50, 40),
(593, 'Jellicent-male', 'Water', 'Ghost', 100, 60, 70, 60),
(594, 'Alomomola', 'Water', NULL, 165, 75, 80, 65),
(595, 'Joltik', 'Bug', 'Electric', 50, 47, 50, 65),
(596, 'Galvantula', 'Bug', 'Electric', 70, 77, 60, 108),
(597, 'Ferroseed', 'Grass', 'Steel', 44, 50, 91, 10),
(598, 'Ferrothorn', 'Grass', 'Steel', 74, 94, 131, 20),
(599, 'Klink', 'Steel', NULL, 40, 55, 70, 30),
(600, 'Klang', 'Steel', NULL, 60, 80, 95, 50),
(601, 'Klinklang', 'Steel', NULL, 60, 100, 115, 90),
(602, 'Tynamo', 'Electric', NULL, 35, 55, 40, 60),
(603, 'Eelektrik', 'Electric', NULL, 65, 85, 70, 40),
(604, 'Eelektross', 'Electric', NULL, 85, 115, 80, 50),
(605, 'Elgyem', 'Psychic', NULL, 55, 55, 55, 30),
(606, 'Beheeyem', 'Psychic', NULL, 75, 75, 75, 40),
(607, 'Litwick', 'Ghost', 'Fire', 50, 30, 55, 20),
(608, 'Lampent', 'Ghost', 'Fire', 60, 40, 60, 55),
(609, 'Chandelure', 'Ghost', 'Fire', 60, 55, 90, 80),
(610, 'Axew', 'Dragon', NULL, 46, 87, 60, 57),
(611, 'Fraxure', 'Dragon', NULL, 66, 117, 70, 67),
(612, 'Haxorus', 'Dragon', NULL, 76, 147, 90, 97),
(613, 'Cubchoo', 'Ice', NULL, 55, 70, 40, 40),
(614, 'Beartic', 'Ice', NULL, 95, 130, 80, 50),
(615, 'Cryogonal', 'Ice', NULL, 80, 50, 50, 105),
(616, 'Shelmet', 'Bug', NULL, 50, 40, 85, 25),
(617, 'Accelgor', 'Bug', NULL, 80, 70, 40, 145),
(618, 'Stunfisk', 'Ground', 'Electric', 109, 66, 84, 32),
(619, 'Mienfoo', 'Fighting', NULL, 45, 85, 50, 65),
(620, 'Mienshao', 'Fighting', NULL, 65, 125, 60, 105),
(621, 'Druddigon', 'Dragon', NULL, 77, 120, 90, 48),
(622, 'Golett', 'Ground', 'Ghost', 59, 74, 50, 35),
(623, 'Golurk', 'Ground', 'Ghost', 89, 124, 80, 55),
(624, 'Pawniard', 'Dark', 'Steel', 45, 85, 70, 60),
(625, 'Bisharp', 'Dark', 'Steel', 65, 125, 100, 70),
(626, 'Bouffalant', 'Normal', NULL, 95, 110, 95, 55),
(627, 'Rufflet', 'Normal', 'Flying', 70, 83, 50, 60),
(628, 'Braviary', 'Normal', 'Flying', 100, 123, 75, 80),
(629, 'Vullaby', 'Dark', 'Flying', 70, 55, 75, 60),
(630, 'Mandibuzz', 'Dark', 'Flying', 110, 65, 105, 80),
(631, 'Heatmor', 'Fire', NULL, 85, 97, 66, 65),
(632, 'Durant', 'Bug', 'Steel', 58, 109, 112, 109),
(633, 'Deino', 'Dark', 'Dragon', 52, 65, 50, 38),
(634, 'Zweilous', 'Dark', 'Dragon', 72, 85, 70, 58),
(635, 'Hydreigon', 'Dark', 'Dragon', 92, 105, 90, 98),
(636, 'Larvesta', 'Bug', 'Fire', 55, 85, 55, 60),
(637, 'Volcarona', 'Bug', 'Fire', 85, 60, 65, 100),
(638, 'Cobalion', 'Steel', 'Fighting', 91, 90, 129, 108),
(639, 'Terrakion', 'Rock', 'Fighting', 91, 129, 90, 108),
(640, 'Virizion', 'Grass', 'Fighting', 91, 90, 72, 108),
(641, 'Tornadus-incarnate', 'Flying', NULL, 79, 115, 70, 111),
(642, 'Thundurus-incarnate', 'Electric', 'Flying', 79, 115, 70, 111),
(643, 'Reshiram', 'Dragon', 'Fire', 100, 120, 100, 90),
(644, 'Zekrom', 'Dragon', 'Electric', 100, 150, 120, 90),
(645, 'Landorus-incarnate', 'Ground', 'Flying', 89, 125, 90, 101),
(646, 'Kyurem', 'Dragon', 'Ice', 125, 130, 90, 95),
(647, 'Keldeo-ordinary', 'Water', 'Fighting', 91, 72, 90, 108),
(648, 'Meloetta-aria', 'Normal', 'Psychic', 100, 77, 77, 90),
(649, 'Genesect', 'Bug', 'Steel', 71, 120, 95, 99);

-- --------------------------------------------------------

--
-- Table structure for table `pokemon_moves`
--

CREATE TABLE `pokemon_moves` (
  `poke_id` int(11) DEFAULT NULL,
  `move_id` int(11) DEFAULT NULL,
  `level_learned` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pokemon_moves`
--

INSERT INTO `pokemon_moves` (`poke_id`, `move_id`, `level_learned`) VALUES
(494, 12, NULL),
(495, 13, NULL),
(496, 13, NULL),
(497, 13, NULL),
(498, 7, NULL),
(499, 7, NULL),
(500, 7, NULL),
(501, 9, NULL),
(502, 9, NULL),
(503, 9, NULL),
(504, 16, NULL),
(505, 16, NULL),
(506, 16, NULL),
(507, 16, NULL),
(508, 16, NULL),
(509, 23, NULL),
(510, 23, NULL),
(511, 13, NULL),
(512, 13, NULL),
(513, 7, NULL),
(514, 7, NULL),
(515, 9, NULL),
(516, 9, NULL),
(517, 12, NULL),
(518, 12, NULL),
(519, 16, NULL),
(520, 16, NULL),
(521, 16, NULL),
(522, 8, NULL),
(523, 8, NULL),
(524, 20, NULL),
(525, 20, NULL),
(526, 20, NULL),
(527, 12, NULL),
(528, 12, NULL),
(529, 10, NULL),
(530, 10, NULL),
(531, 16, NULL),
(532, 19, NULL),
(533, 19, NULL),
(534, 19, NULL),
(535, 9, NULL),
(536, 9, NULL),
(537, 9, NULL),
(538, 19, NULL),
(539, 19, NULL),
(540, 21, NULL),
(541, 21, NULL),
(542, 21, NULL),
(543, 21, NULL),
(544, 21, NULL),
(545, 21, NULL),
(546, 13, NULL),
(547, 13, NULL),
(548, 13, NULL),
(549, 13, NULL),
(550, 9, NULL),
(551, 10, NULL),
(552, 10, NULL),
(553, 10, NULL),
(554, 7, NULL),
(555, 7, NULL),
(556, 13, NULL),
(557, 21, NULL),
(558, 21, NULL),
(559, 23, NULL),
(560, 23, NULL),
(561, 12, NULL),
(562, 15, NULL),
(563, 15, NULL),
(564, 9, NULL),
(565, 9, NULL),
(566, 20, NULL),
(567, 20, NULL),
(568, 17, NULL),
(569, 17, NULL),
(570, 23, NULL),
(571, 23, NULL),
(572, 16, NULL),
(573, 16, NULL),
(574, 12, NULL),
(575, 12, NULL),
(576, 12, NULL),
(577, 12, NULL),
(578, 12, NULL),
(579, 12, NULL),
(580, 9, NULL),
(581, 9, NULL),
(582, 11, NULL),
(583, 11, NULL),
(584, 11, NULL),
(585, 16, NULL),
(586, 16, NULL),
(587, 8, NULL),
(588, 21, NULL),
(589, 21, NULL),
(590, 13, NULL),
(591, 13, NULL),
(592, 9, NULL),
(593, 9, NULL),
(594, 9, NULL),
(595, 21, NULL),
(596, 21, NULL),
(597, 13, NULL),
(598, 13, NULL),
(599, 22, NULL),
(600, 22, NULL),
(601, 22, NULL),
(602, 8, NULL),
(603, 8, NULL),
(604, 8, NULL),
(605, 12, NULL),
(606, 12, NULL),
(607, 15, NULL),
(608, 15, NULL),
(609, 15, NULL),
(610, 14, NULL),
(611, 14, NULL),
(612, 14, NULL),
(613, 11, NULL),
(614, 11, NULL),
(615, 11, NULL),
(616, 21, NULL),
(617, 21, NULL),
(618, 10, NULL),
(619, 19, NULL),
(620, 19, NULL),
(621, 14, NULL),
(622, 10, NULL),
(623, 10, NULL),
(624, 23, NULL),
(625, 23, NULL),
(626, 16, NULL),
(627, 16, NULL),
(628, 16, NULL),
(629, 23, NULL),
(630, 23, NULL),
(631, 7, NULL),
(632, 21, NULL),
(633, 23, NULL),
(634, 23, NULL),
(635, 23, NULL),
(636, 21, NULL),
(637, 21, NULL),
(638, 22, NULL),
(639, 20, NULL),
(640, 13, NULL),
(641, 18, NULL),
(642, 8, NULL),
(643, 14, NULL),
(644, 14, NULL),
(645, 10, NULL),
(646, 14, NULL),
(647, 9, NULL),
(648, 16, NULL),
(649, 21, NULL),
(494, 7, NULL),
(499, 19, NULL),
(500, 19, NULL),
(519, 18, NULL),
(520, 18, NULL),
(521, 18, NULL),
(527, 18, NULL),
(528, 18, NULL),
(530, 22, NULL),
(536, 10, NULL),
(537, 10, NULL),
(540, 13, NULL),
(541, 13, NULL),
(542, 13, NULL),
(543, 17, NULL),
(544, 17, NULL),
(545, 17, NULL),
(546, 24, NULL),
(547, 24, NULL),
(551, 23, NULL),
(552, 23, NULL),
(553, 23, NULL),
(557, 20, NULL),
(558, 20, NULL),
(559, 19, NULL),
(560, 19, NULL),
(561, 18, NULL),
(564, 20, NULL),
(565, 20, NULL),
(566, 18, NULL),
(567, 18, NULL),
(580, 18, NULL),
(581, 18, NULL),
(585, 13, NULL),
(586, 13, NULL),
(587, 18, NULL),
(589, 22, NULL),
(590, 17, NULL),
(591, 17, NULL),
(592, 15, NULL),
(593, 15, NULL),
(595, 8, NULL),
(596, 8, NULL),
(597, 22, NULL),
(598, 22, NULL),
(607, 7, NULL),
(608, 7, NULL),
(609, 7, NULL),
(618, 8, NULL),
(622, 15, NULL),
(623, 15, NULL),
(624, 22, NULL),
(625, 22, NULL),
(627, 18, NULL),
(628, 18, NULL),
(629, 18, NULL),
(630, 18, NULL),
(632, 22, NULL),
(633, 14, NULL),
(634, 14, NULL),
(635, 14, NULL),
(636, 7, NULL),
(637, 7, NULL),
(638, 19, NULL),
(639, 19, NULL),
(640, 19, NULL),
(642, 18, NULL),
(643, 7, NULL),
(644, 8, NULL),
(645, 18, NULL),
(646, 11, NULL),
(647, 19, NULL),
(648, 12, NULL),
(649, 22, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`item_id`);

--
-- Indexes for table `moves_base`
--
ALTER TABLE `moves_base`
  ADD PRIMARY KEY (`move_id`);

--
-- Indexes for table `player_inventory`
--
ALTER TABLE `player_inventory`
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `player_pokemon`
--
ALTER TABLE `player_pokemon`
  ADD PRIMARY KEY (`instance_id`),
  ADD KEY `player_id` (`player_id`),
  ADD KEY `player_pokemon_ibfk_2` (`poke_id`);

--
-- Indexes for table `player_save`
--
ALTER TABLE `player_save`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pokemon_base`
--
ALTER TABLE `pokemon_base`
  ADD PRIMARY KEY (`poke_id`);

--
-- Indexes for table `pokemon_moves`
--
ALTER TABLE `pokemon_moves`
  ADD KEY `poke_id` (`poke_id`),
  ADD KEY `move_id` (`move_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `moves_base`
--
ALTER TABLE `moves_base`
  MODIFY `move_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `player_pokemon`
--
ALTER TABLE `player_pokemon`
  MODIFY `instance_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `player_save`
--
ALTER TABLE `player_save`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `player_inventory`
--
ALTER TABLE `player_inventory`
  ADD CONSTRAINT `player_inventory_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`);

--
-- Constraints for table `player_pokemon`
--
ALTER TABLE `player_pokemon`
  ADD CONSTRAINT `player_pokemon_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `player_save` (`id`),
  ADD CONSTRAINT `player_pokemon_ibfk_2` FOREIGN KEY (`poke_id`) REFERENCES `pokemon_base` (`poke_id`) ON DELETE CASCADE;

--
-- Constraints for table `pokemon_moves`
--
ALTER TABLE `pokemon_moves`
  ADD CONSTRAINT `pokemon_moves_ibfk_1` FOREIGN KEY (`poke_id`) REFERENCES `pokemon_base` (`poke_id`),
  ADD CONSTRAINT `pokemon_moves_ibfk_2` FOREIGN KEY (`move_id`) REFERENCES `moves_base` (`move_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
