-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 15, 2026 at 11:07 AM
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
(4, 'Vine Whip', 45, 100, 'GRASS', 'SPECIAL');

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
(1, 1, 4, 5, 54, 60);

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
(1, 'Bulbasaur', 'GRASS', 'POISON', 45, 49, 49, 45),
(4, 'Charmander', 'FIRE', NULL, 39, 52, 43, 65),
(7, 'Squirtle', 'WATER', NULL, 44, 48, 65, 43);

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
(1, 1, 1),
(1, 4, 5),
(4, 1, 1),
(4, 2, 5);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `moves_base`
--
ALTER TABLE `moves_base`
  ADD PRIMARY KEY (`move_id`);

--
-- Indexes for table `player_pokemon`
--
ALTER TABLE `player_pokemon`
  ADD PRIMARY KEY (`instance_id`),
  ADD KEY `player_id` (`player_id`),
  ADD KEY `poke_id` (`poke_id`);

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
-- AUTO_INCREMENT for table `moves_base`
--
ALTER TABLE `moves_base`
  MODIFY `move_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `player_pokemon`
--
ALTER TABLE `player_pokemon`
  MODIFY `instance_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `player_save`
--
ALTER TABLE `player_save`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `player_pokemon`
--
ALTER TABLE `player_pokemon`
  ADD CONSTRAINT `player_pokemon_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `player_save` (`id`),
  ADD CONSTRAINT `player_pokemon_ibfk_2` FOREIGN KEY (`poke_id`) REFERENCES `pokemon_base` (`poke_id`);

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
