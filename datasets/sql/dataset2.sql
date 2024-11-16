CREATE TABLE Zaposleni (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ime VARCHAR(50) NOT NULL,
    prezime VARCHAR(50) NOT NULL,
    godina INT NOT NULL,
    plata DECIMAL(10, 2) NOT NULL
);

-- Popunjavanje tabele sa 30 redova
INSERT INTO Zaposleni (ime, prezime, godina, plata)
VALUES
('Milan', 'Mitrović', 25, 65000.00),
('Jelena', 'Stefanović', 32, 72000.50),
('Vladimir', 'Radović', 28, 54000.75),
('Sara', 'Petrović', 21, 48000.00),
('Marko', 'Đorđević', 35, 81000.25),
('Milica', 'Kostić', 29, 67000.00),
('Nikola', 'Perić', 26, 60000.00),
('Tamara', 'Lukić', 31, 73000.00),
('Stefan', 'Obradović', 27, 59000.50),
('Ana', 'Jovanović', 24, 51000.75),
('Dušan', 'Marić', 30, 69000.00),
('Ivana', 'Nikolić', 23, 52000.00),
('Luka', 'Pavlović', 36, 85000.00),
('Katarina', 'Bogdanović', 29, 64000.00),
('Andrej', 'Savić', 33, 76000.00),
('Nina', 'Živanović', 22, 49000.25),
('Miloš', 'Vasić', 28, 58000.75),
('Teodora', 'Stanković', 25, 63000.00),
('Filip', 'Radovanović', 34, 82000.50),
('Marija', 'Kovačević', 27, 61000.00),
('Aleksandar', 'Lazarević', 32, 70000.00),
('Tijana', 'Milovanović', 26, 56000.25),
('Vanja', 'Jevtić', 31, 75000.75),
('Nemanja', 'Milenković', 30, 68000.00),
('Kristina', 'Matić', 23, 53000.00),
('Ognjen', 'Stevanović', 28, 60000.50),
('Anđela', 'Damjanović', 25, 62000.00),
('Mihailo', 'Ristić', 29, 65000.00),
('Sofija', 'Mitrović', 24, 50000.00),
('Vladimir', 'Radosavljević', 33, 78000.00);