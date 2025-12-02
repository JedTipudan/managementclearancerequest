-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Nov 18, 2025 at 09:45 AM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cms`
--

-- --------------------------------------------------------

--
-- Table structure for table `clearancerequest`
--

DROP TABLE IF EXISTS `clearancerequest`;
CREATE TABLE IF NOT EXISTS `clearancerequest` (
  `clearancerequestID` int NOT NULL AUTO_INCREMENT,
  `studentID` int NOT NULL,
  `acadyear` varchar(50) NOT NULL,
  `semester` varchar(50) NOT NULL,
  `dateRequested` datetime NOT NULL,
  PRIMARY KEY (`clearancerequestID`)
) ENGINE=MyISAM AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `clearancerequest`
--

INSERT INTO `clearancerequest` (`clearancerequestID`, `studentID`, `acadyear`, `semester`, `dateRequested`) VALUES
(42, 6, '2025-2026', 'Second Sem', '2025-11-11 17:10:09'),
(41, 6, '2025-2026', 'First Sem', '2025-11-11 17:09:54'),
(40, 3, '2022-2023', 'Second Sem', '2025-11-11 16:20:13'),
(39, 3, '2024-2025', 'First Sem', '2025-11-11 16:19:47'),
(38, 3, '2025-2026', 'First Sem', '2025-11-11 16:19:29'),
(37, 3, '2025-2026', 'Second Sem', '2025-11-11 16:19:11');

-- --------------------------------------------------------

--
-- Table structure for table `clearancestatus`
--

DROP TABLE IF EXISTS `clearancestatus`;
CREATE TABLE IF NOT EXISTS `clearancestatus` (
  `clearancestatusID` int NOT NULL AUTO_INCREMENT,
  `clearancerequestID` int NOT NULL,
  `clearingofficesID` int NOT NULL,
  `status` varchar(50) NOT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `approvedBy` int DEFAULT NULL,
  `dateCleared` datetime DEFAULT NULL,
  PRIMARY KEY (`clearancestatusID`)
) ENGINE=MyISAM AUTO_INCREMENT=283 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `clearancestatus`
--

INSERT INTO `clearancestatus` (`clearancestatusID`, `clearancerequestID`, `clearingofficesID`, `status`, `remarks`, `approvedBy`, `dateCleared`) VALUES
(282, 42, 6, 'Pending', NULL, NULL, NULL),
(281, 42, 5, 'Pending', NULL, NULL, NULL),
(280, 42, 4, 'Pending', NULL, NULL, NULL),
(279, 42, 3, 'Pending', NULL, NULL, NULL),
(278, 42, 2, 'Pending', NULL, NULL, NULL),
(277, 42, 1, 'Pending', NULL, NULL, NULL),
(276, 41, 6, 'Pending', NULL, NULL, NULL),
(275, 41, 5, 'Pending', NULL, NULL, NULL),
(274, 41, 4, 'Pending', NULL, NULL, NULL),
(273, 41, 3, 'Pending', NULL, NULL, NULL),
(272, 41, 2, 'Pending', NULL, NULL, NULL),
(271, 41, 1, 'Pending', NULL, NULL, NULL),
(270, 40, 6, 'Pending', NULL, NULL, NULL),
(269, 40, 5, 'Pending', NULL, NULL, NULL),
(268, 40, 4, 'Pending', NULL, NULL, NULL),
(267, 40, 3, 'Pending', NULL, NULL, NULL),
(266, 40, 2, 'Pending', NULL, NULL, NULL),
(265, 40, 1, 'Pending', NULL, NULL, NULL),
(264, 39, 6, 'Pending', NULL, NULL, NULL),
(263, 39, 5, 'Pending', NULL, NULL, NULL),
(262, 39, 4, 'Pending', NULL, NULL, NULL),
(261, 39, 3, 'Pending', NULL, NULL, NULL),
(260, 39, 2, 'Pending', NULL, NULL, NULL),
(259, 39, 1, 'Pending', NULL, NULL, NULL),
(258, 38, 6, 'Pending', NULL, NULL, NULL),
(257, 38, 5, 'Pending', NULL, NULL, NULL),
(256, 38, 4, 'Pending', NULL, NULL, NULL),
(255, 38, 3, 'Pending', NULL, NULL, NULL),
(254, 38, 2, 'Pending', NULL, NULL, NULL),
(253, 38, 1, 'Pending', NULL, NULL, NULL),
(252, 37, 6, 'Pending', NULL, NULL, NULL),
(251, 37, 5, 'Pending', NULL, NULL, NULL),
(250, 37, 4, 'Pending', NULL, NULL, NULL),
(249, 37, 3, 'Pending', NULL, NULL, NULL),
(248, 37, 2, 'Pending', NULL, NULL, NULL),
(247, 37, 1, 'Pending', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `clearingoffices`
--

DROP TABLE IF EXISTS `clearingoffices`;
CREATE TABLE IF NOT EXISTS `clearingoffices` (
  `clearingofficesID` int NOT NULL AUTO_INCREMENT,
  `clearingoffice` varchar(50) NOT NULL,
  `userID` int NOT NULL,
  PRIMARY KEY (`clearingofficesID`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `clearingoffices`
--

INSERT INTO `clearingoffices` (`clearingofficesID`, `clearingoffice`, `userID`) VALUES
(1, 'Office Dean', 7),
(2, 'Library Office', 8),
(3, 'Science Laboratory Office', 9),
(4, 'Accounting Office', 10),
(5, 'Office Student Affairs', 11),
(6, 'Registrar', 12);

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
CREATE TABLE IF NOT EXISTS `student` (
  `studentID` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `idno` varchar(50) NOT NULL,
  `fullname` varchar(500) NOT NULL,
  `course` varchar(50) NOT NULL,
  `gender` varchar(50) NOT NULL,
  `dateRegistered` datetime NOT NULL,
  PRIMARY KEY (`studentID`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`studentID`, `userID`, `idno`, `fullname`, `course`, `gender`, `dateRegistered`) VALUES
(3, 3, '2023-0659', 'Janne Denzelle  Tagupa', 'BSIT', 'Male', '2025-11-06 16:57:42'),
(4, 4, '2022-1234', 'tae', 'BSIT', 'Male', '2025-11-06 17:02:24'),
(5, 5, '1111-1111', 'belat', 'BITM', 'Female', '2025-11-06 21:34:09'),
(6, 6, '2222-2222', '123', 'BSIT', 'Male', '2025-11-11 17:09:32'),
(7, 7, '0000-0000', 'dean', 'BSIT', 'Male', '2025-11-18 16:31:56'),
(8, 8, '1000-0000', 'Library', 'BSIT', 'Male', '2025-11-18 16:34:58'),
(9, 9, '3000-0000', 'SciLab', 'BSIT', 'Male', '2025-11-18 16:35:59'),
(10, 10, '4000-0000', 'Accounting', 'BSIT', 'Male', '2025-11-18 16:36:38'),
(11, 11, '5000-0000', 'OSA', 'BSIT', 'Male', '2025-11-18 16:37:04'),
(12, 12, '6000-0000', 'Registrar', 'BSIT', 'Male', '2025-11-18 16:37:48');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `fullname` varchar(500) NOT NULL,
  `username` blob NOT NULL,
  `password` blob NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`userID`, `fullname`, `username`, `password`, `type`) VALUES
(3, 'Janne Denzelle  Tagupa', 0x61a2db354b2b5cbf1a249a317f43a7f4, 0xe14657529ad5f5f261213203f907f437, 'student'),
(4, 'tae', 0x94f4ff0d60646c8dcb938cbf0c4d66d2, 0x94f4ff0d60646c8dcb938cbf0c4d66d2, 'student'),
(5, 'belat', 0xd785aa28e474fc43e7050e4a2e03a056, 0xd785aa28e474fc43e7050e4a2e03a056, 'student'),
(6, '123', 0xa6f774aebee2c91c4450d0f6b4ba94b1, 0xa6f774aebee2c91c4450d0f6b4ba94b1, 'student'),
(7, 'Dean', 0x30f1a7e70108f6e9269ef348311d30d7, 0x30f1a7e70108f6e9269ef348311d30d7, 'dean'),
(8, 'Library', 0xc70ff28f5180d7c0924db2a1b4e100db, 0xc70ff28f5180d7c0924db2a1b4e100db, 'library'),
(9, 'SciLab', 0xf717a1687749a4f245b5e74e76d29377, 0xf717a1687749a4f245b5e74e76d29377, 'scilab'),
(10, 'Accounting', 0x03358b8af2499c31661ee3cb8c32b04f, 0x03358b8af2499c31661ee3cb8c32b04f, 'accounting'),
(11, 'OSA', 0x5a2ed11f4ad967cf6c81a674b362fe67, 0x5a2ed11f4ad967cf6c81a674b362fe67, 'osa'),
(12, 'Registrar', 0x0f2984fb3d0016bd7846fac497e31cc9, 0x0f2984fb3d0016bd7846fac497e31cc9, 'registrar');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
