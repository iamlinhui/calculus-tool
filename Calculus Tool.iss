; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "���˷�������"
#define MyAppEnglishName "Calculus Tool"
#define MyAppVersion "1.0.3"
#define MyAppPublisher "Lynn"
#define MyAppURL "https://www.lexin.com/"
#define MyAppExeName "calculus-tool.exe"
#define MySourcePath "out\artifacts\calculus_tool\bundles\calculus-tool\"

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{8042403B-D99E-4A88-8723-9E03446F4C83}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppEnglishName}
DisableProgramGroupPage=yes
; Remove the following line to run in administrative install mode (install for all users.)
PrivilegesRequired=lowest
OutputDir=D:\
OutputBaseFilename=Calculus Tool Setup {#MyAppVersion}
SetupIconFile={#MySourcePath}\calculus-tool.ico
UninstallDisplayIcon={app}\calculus-tool.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
; Name: "english"; MessagesFile: "compiler:Default.isl"
; https://github.com/jrsoftware/issrc/blob/main/Files/Languages/Unofficial/ChineseSimplified.isl
Name:"cn";MessagesFile:"compiler:Languages\ChineseSimplified.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "{#MySourcePath}\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#MySourcePath}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

