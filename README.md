# Loader for BookFinder app

### Overview
ETL job created in Spring boot. Its main purpose is to download data from Google Sheets, 
map them to JSON file that are then stored on Google Drive. After that, hugo website
is rebuilt and changes are pushed to the [website repo](https://github.com/snap22/BookFinder-website).

The final website is available at https://snap22.github.io/BookFinder-website/

#### Dependencies
- Spring boot
- Google Sheets Service API
- Google Drive Service API
- GitHub API
- Git API

#### Setup
To run this project locally, **Google Service Account** has to be set up with the required permissions. The service account key 
should be stored as JSON and passed as argument for variable `google.service.account.credentials.path`
Additionally, a `.env` file should be created, having these variables:
- `GOOGLE_DRIVE_FOLDER_ID` - ID of the Google Drive folder
- `GOOGLE_SHEETS_SPREADSHEET_ID` - ID of the Google Sheets
- `GITHUB_OAUTH` - Access token for GitHub
- `GITHUB_USER` - Username for GitHub under which the API is called

The Google Sheets app has the following structure:

| isbn  | author  |  name  | 
|---|---|---|
| ISBN of the book (optional) | Author of the book  | Name of the book  |

*Note: each sheet is considered as a different user*
