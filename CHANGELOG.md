# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased] - 2025-12-26

### Added
- Add user authentication module (to be refined)
- Add exception handling module
- Add database support

### Changed
- Code enhancement
	* Enhance Optional chain processing capabilities

### Fixed
- Fix the error of mp3 media type

### Removed


## [Unreleased] - 2025-12-07

### Added

### Changed
- Code enhancement
- Refactor AudioType Class
- Disable Unnecessary Startup Logs
- New Optional Utility Class
	* Created a custom Optional (xyz.fayvox.music.common.Optional) class based on the design flaws of JDK 8 java.util.Optional
	* Added null-coalescing handling for chained methods, enabling smoother null-value merging in chained calls

### Fixed
- Update Project Package Name
	* Adjusted the project's base package name, migrating from the original package name to the new package name xyz.fayvox.music to unify naming conventions

### Removed


## [Unreleased] - 2025-11-30

### Added

### Changed
- Code enhancement

### Fixed

### Removed


## [Unreleased] - 2025-11-28

### Added
- Support asynchronous requests
- Add new music service module
- Add more music formats support (AAC, MP3, WAV)
- Add keyword search, artist search and artists categories

### Changed
- Move all requests to music service for processing

### Fixed

### Removed


## [Unreleased] - 2025-11-24

### Added
- Initial Music Server: First runnable version of the personal music steaming server
- Core Audio Support: Basic playback support for common audio formats (FLAC)
- REST API Endpoints:
	* GET /music - List all music type or music (e.g., `/music?type=pop`, `/music?type=pop&name=song.flac`)

### Changed
- Change CHANGELOG to CHANGELOG.md

### Fixed

### Removed


## [Unreleased] - 2025-11-03

### Added
- Initial commit

### Changed

### Fixed

### Removed
