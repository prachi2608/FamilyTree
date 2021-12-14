CREATE TABLE `personinfo` (
  `personid` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`personid`));
  
  CREATE TABLE `mediainfo` (
  `mediaid` INT NOT NULL AUTO_INCREMENT,
  `filelocation` VARCHAR(100) NOT NULL,
  `filename` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`mediaid`));

CREATE TABLE `personattributes` (
  `personid` INT NOT NULL,
  `dateofbirth` VARCHAR(45) NULL,
  `placeofbirth` VARCHAR(45) NULL,
  `gender` VARCHAR(45) NULL,
  `occupation` VARCHAR(45) NULL,
  `dateofdeath` VARCHAR(45) NULL,
  `placeofdeath` VARCHAR(45) NULL,
  PRIMARY KEY (`personid`),
  CONSTRAINT `attribute_id`
    FOREIGN KEY (`personid`)
    REFERENCES `personinfo` (`personid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

CREATE TABLE `mediaattributes` (
  `mediaid` INT NOT NULL,
  `location` VARCHAR(45) NULL,
  `dateofpicture` VARCHAR(45) NULL,
  PRIMARY KEY (`mediaid`),
  CONSTRAINT `media_fk`
    FOREIGN KEY (`mediaid`)
    REFERENCES `mediainfo` (`mediaid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
CREATE TABLE `recordnotes` (
  `personid` INT NOT NULL,
  `note` VARCHAR(200) NOT NULL,
  `lastUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`personid`)
    REFERENCES `personinfo` (`personid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
CREATE TABLE `recordreferences` (
  `personid` INT NOT NULL,
  `personreferences` VARCHAR(45) NOT NULL,
  `lastUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`personid`)
    REFERENCES `personinfo` (`personid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

CREATE TABLE `parentchildrelation` (
  `parent` INT NOT NULL,
  `child` INT NOT NULL,
  CONSTRAINT `parent_fk`
    FOREIGN KEY (`parent`)
    REFERENCES `personinfo` (`personid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `child_fk`
    FOREIGN KEY (`child`)
    REFERENCES `personinfo` (`personid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

    
CREATE TABLE `partneringstatus` (
  `partner1` INT NOT NULL,
  `partner2` INT NOT NULL,
  `relationstatus` VARCHAR(45) NOT NULL,
  CONSTRAINT `p1_fk`
    FOREIGN KEY (`partner1`)
    REFERENCES `personinfo` (`personid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `p2_fk`
    FOREIGN KEY (`partner2`)
    REFERENCES `personinfo` (`personid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
CREATE TABLE `recordtags` (
  `mediaid` INT NOT NULL,
  `tags` VARCHAR(45) NOT NULL,
  CONSTRAINT `mediaid_fk1`
    FOREIGN KEY (`mediaid`)
    REFERENCES `mediainfo` (`mediaid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);


CREATE TABLE `personinmedia` (
  `personid` INT NOT NULL,
  `mediaid` INT NOT NULL,
  CONSTRAINT `pfk_1`
    FOREIGN KEY (`personid`)
    REFERENCES `personinfo` (`personid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `mfk_1`
    FOREIGN KEY (`mediaid`)
    REFERENCES `mediainfo` (`mediaid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

