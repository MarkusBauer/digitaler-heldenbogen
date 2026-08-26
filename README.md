Digitaler Heldenbogen
=====================

Dieses Plugin für die DSA4 Helden-Software von [helden-software.de](https://www.helden-software.de) erstellt kompakte
Heldenbögen als HTML oder PDF.
Mit dabei ist auch ein automatischer Export (bspw. in Dropbox).

[**Website / Beispiele**](https://mk-bauer.de/helden-software/heldenbogen.html) |
[**Download**](https://mk-bauer.de/helden-software/dl.php?pw=digitalerheldenbogen&filename=digitaler-heldenbogen.jar)

Nach Installation aktualisiert sich das Plugin automatisch.


Installation
------------
Die Datei muss in den `plugins`-Ordner der Helden-Software kopiert werden. 
Dieser liegt unter `C:\Users\<dein-name>\helden\plugins` (Windows) oder unter `/home/<dein-name>/helden/plugins` (Linux). 
Nach einem Neustart der Helden-Software sind neue Einträge im Erweiterungen-Menü verfügbar.

Die Erweiterung funktioniert nur mit der Helden-Software Version **5.6.0 oder neuer**.


Entwickler
----------

Zum Bauen des Plugins sind Java, Gradle, NodeJS und npm nötig. Alles Weitere wird automatisch heruntergeladen.
```shell
./gradlew shadowJar
```

Die wichtigsten Orte zur Übersicht:
- [/src/main/resources/templates](./src/main/resources/templates) - hier liegen die Templates der Bögen (was wo steht)
- [/heldenbogen](./heldenbogen) - hier liegen die Styles und Scripte, die das Aussehen der Bögen bestimmen
- [/src/main/java/de/mb/heldenbogen/Renderer.java](./src/main/java/de/mb/heldenbogen/Renderer.java) Einstiegspunkt für den Java-Teil der Bögen
- [/src/main/java/de/mb/heldenbogen/Shortener.java](./src/main/java/de/mb/heldenbogen/Shortener.java) Abkürzungen zum Platz sparen werden hier definiert
- [/src/main/java/de/mb/heldenbogen/HeldenbogenManualExporter.java](./src/main/java/de/mb/heldenbogen/HeldenbogenManualExporter.java) CLI Tool zum Entwickeln. Konvertiert alle Helden-XML im Ordner `demo` in HTML- und PDF-Bögen.

Nützliche Tricks beim Entwickeln:
- den `HeldenbogenManualExporter` direkt aus der IDE ausführen, um Test-Bögen neu zu erstellen (ohne sich durch die Helden-Software klicken zu müssen)
- die vom Exporter generierten `.html`-Bögen nutzen die aktuellen Styles aus dem build-Ordner (im Gegensatz zu `.full.html` welche die Styles integriert haben)
- `cd heldenbogen ; npm run watch` compiliert die Styles bei jeder Änderung
- in Kombination lassen sich Style-Änderungen schnell testen: scss im Editor speichern, im Browser reicht dann F5 drücken


Contributing
------------

Ich nehme gerne Bugfixes und kleine Verbesserungen als PRs.

Neue Features bitte vorher diskutieren (als Issue oder per Mail/Discord) bevor ihr euch die Mühe macht (oder im Zweifelsfall forken).

Die Gestaltung der Bögen ist ein sehr subjektives Thema, bei dem verschiedene Spielertypen sehr verschiedene Meinungen haben.
Die vorliegenden Bögen sind auf meinen Spielstil (und den meiner Runde) zugeschnitten.
Größere Änderungen oder Umgestaltungen sind meist nichts, was wieder in diesem Repository landen sollte - ein Fork mit euren Präferenzen wäre dann die passendere Alternative.
Bitte nicht enttäuscht sein, wenn ich PRs mit Umgestaltungen ablehne.

Unabhängig davon sind Forks gerne gesehen - teilt mir gerne mit wenn ihr was Neues gebastelt habt, Inspiration nehm ich gerne.


Kontakt
-------

- "Rothen" bei [dsaforum.de](http://dsaforum.de/memberlist.php?mode=viewprofile&u=12050) | [Thread](https://dsaforum.de/TODO)
- Per Mail: markus-7y5wrhdz (AT) mk-bauer.de
- Github: [MarkusBauer/digitaler-heldenbogen](https://github.com/MarkusBauer/digitaler-heldenbogen)
