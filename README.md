# react-native-intent-launcher

## Getting started
Pacote desevolvido para abrir arquivos da pasta Downloads, desde que se saiba o nome do mesmo. O Dispositivo mostrara ao usuario os aplicativos que podem abrir aquele arquivo.


## Getting started

`$ npm i -s react-native-intent-launcher`

### Mostly automatic installation

`$ react-native link react-native-intent-launcher`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`

- Add `import com.thebylito.RNFileLauncherPackage;` to the imports at the top of the file
- Add `new RNFileLauncherPackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:
   ```
   include ':react-native-intent-launcher'
   project(':react-native-intent-launcher').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-intent-launcher/android')
   ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
	```
	compile project(':react-native-intent-launcher')
	```

4. Adicionar em `AndroidManifest.xml`

	```diff
	<application
		...
		>
	+ <provider
	+	android:name="android.support.v4.content.FileProvider"
	+	android:authorities="${applicationId}.provider"
	+	android:exported="false"
	+	android:grantUriPermissions="true">
	+	<meta-data
	+		android:name="android.support.FILE_PROVIDER_PATHS"
	+		android:resource="@xml/provider_paths" />
	+	</provider>
	</application>
	```

## Usage

```javascript
import RNFileLauncher from 'react-native-intent-launcher';

try {
  await RNFileLauncher.startActivity({ fileName }); // Que esta dentro da pasta downloads;
} catch (e) {
  /* Se não existir um programa para abrir a Promisse é rejeitada */
  console.log(e);
}
```
