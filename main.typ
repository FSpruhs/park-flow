#set page(
    width: 21cm,
    height: 29.7cm,
    margin: 2.5cm,
    numbering: none,
)

#align(left)[
    Fabian Spruhs \
    Lütticher Str. 57 \
    50674 Köln \
    fabian\@spruhs.com \
    Matr.-Nr: 3568695 \
]

#v(4cm)

#align(center)[

    #set text(size: 14pt, weight: "regular")
    Bachelorarbeit im Studiengang\
    Bachelor of Science
    #v(4cm)

    #set text(size: 18pt, weight: "bold")
    Implementierung und Evaluierung eines Event-Sourcing-Ansatzes mit Spring Boot und Kotlin in einer modularen DDD-Architektur \

    #v(4cm)
    #set text(size: 14pt, weight: "regular")
    Betreuerin: Dr. Daniela Keller\
    \
    Fernuniversität in Hagen \
    Wintersemester 2025/2026

]

#pagebreak()

#set page(
  margin: (
    top: 4cm,
    bottom: 4cm,
    left: 3cm,
    right: 3cm,
  ),
  numbering: "1"
)

#set text(
    size: 11pt,
    hyphenate: true,
    lang: "de",

)
#set par(leading: 0.6em)
#set heading(numbering: "1.")
#outline(
    depth: 3,
     title: "Inhaltsverzeichnis",
)

#pagebreak()

= Abkürzungen
\
#table(
    columns: (auto, auto),
    inset: 10pt,
    align: horizon,
    stroke: (x: none, y: none),
    table.header([*Abkürzung*], [*Bedeutung*]),

    "DDD", "Domain driven design",
    "ES", "Event Sourcing",
    "EDA", "Event Driven Architecture",
    "CQRS", "Command Query Responsibility Segregation",
)

#pagebreak()

= Einleitung
\
Events spielen in der modernen Softwareentwicklung eine immer grössere Rolle.
Anwendungen werden zunehmend reaktiver, ereignisgetrieben und asynchron gestaltet.
In dieser Arbeit sollen verschiedene Techniken, die auf Events basieren miteinander kombiniert und praktisch umgesetzt werden.

Events sind Zustandsänderungen oder Nachrichten, die innerhalb eines Systems auftreten.
Anwendungen, die auf Events basieren, unterscheiden sich deutlich von klassischen, imperativen Anwendungen.
Sie ermöglichen eine Entkoppelung zwischen Komponenten, fördern asynchrone Kommunikation und erleichtern die Erweiterbarkeit.
Durch Events können Systeme flexibler auf neue Anforderungen reagieren und komplexe Prozesse besser abbilden.

Domain-Driven Design (DDD) bietet einen übergeordneten Rahmen, um komplexe Anwendungen systematisch zu modellieren.
Innerhalb von DDD spielen Events, insbesondere Domain-Events, eine zentrale Rolle, da sie geschäftsrelevante Zustandsänderungen widerspiegeln.
Werkzeuge wie Event Storming unterstützen die Modellierung von Domains, indem sie Events als Ausgangspunkt für die Analyse und Gestaltung von Prozessen nutzten.

Event Sourcing ist eine alternative Methode der Persistierung, bei der nicht der aktuell Zustand, sondern die Abfolge von Events gespeichert wird.
Vorteile sind unter anderem:

- Ein vollständiges, nachvollziehbares Protokoll aller Zustandsänderungen
- Rückverfolgbarkeit und Reproduzierbarkeit von Systemzuständen
- Unterstützung von asynchronen Architekturen und CQRS(Command Query Responsibility Segregation)

Durch Event Sourcing entsteht eine wertvolle Ereignishistorie.
Solche historischen Daten können in der modernen Informationsverarbeitung vielfältig genutzt werden.
Beispielsweise für Analysen, Prognosen oder Machine-Learning-Anwendungen.

Kombiniert bieten DDD, und Event Sourcing klare Vorteile.
Modularität durch Entkoppelung der komponenten, asynchrone Verarbeitung von Ereignissen sowie Flexibilität und Erweiterbarkeit der Software.

In dieser Arbeit werden die Konzepte an einer beispielhaften Anwendung umgesetzt.
Als Technologien werden Kotlin und Spring Boot verwendet:

- *Kotlin*: Moderne Sprache für die JVM, hohe Lesbarkeit, null-sichere Typen und gute Unterstützung für funktionale Programmierung.
 Im Vergleich zu Java bietet Kotlin eine deutlich bessere Unterstützung für Nebenläufigkeit, was für ereignisgetriebene Architekturen entscheidend ist.
- *Spring Boot*: Starke Unterstützung für modulare Anwendungen, einfache Konfiguration von Microservices oder modularen Monolithen sowie ein reichhaltiges Ökosystem für Event-Verarbeitung.
 Spring bietet darüber hinaus ein eigenes internes Event-System, das die Kommunikation zwischen Komponenten erleichtert, und unterstützt sowohl auf der Controller- als auch auf der Persistenz-Ebene reactive Programmierung.
- *Modularer Monolith*: Vereint die Vorteile der Modularität mit einer einfacheren Deployment-Strategie.
- *Eigener Event Store*: Ermöglicht die vollständige Kontrolle über Persistierung und Event-Verarbeitung.

Die Evaluierung soll zeigen, inwiefern der Event-Sourcing-Ansatz mit DDD und Spring Boot/Kotlin die erwarteten Vorteile realisiert.
Die Evaluationskriterien sind die Performance der Event-Verarbeitung, die Modularität und Entkopplung der Komponenten, die Fehler- und Wiederherstellbarkeit aus dem Event Store sowie die Nachvollziehbarkeit und Konsistenz der Ereignisprotokollierung.

Die Arbeit gliedert sich in die Vorstellung der theoretischen Grundlagen, die Umsetzung in einer Beispielanwendung und die Evaluierung der gewählten Architektur.-

= Theoretische Grundlagen

In diesem Kapitel werden die theoretischen Grundlagen vorgestellt, die für die Umsetzung der in dieser Arbeit entwickelten Softwarelösung relevant sind.
Ziel ist es, ein solides Verständnis der zentralen Konzepte und Technologien zu vermitteln, die im anschließenden Kapitel gemeinsam in einem Programm praktisch implementiert werden.
Dabei soll deutlich werden, wie die einzelnen Ansätze ineinandergreifen und sich gegenseitig ergänzen, um flexible, skalierbare und gut strukturierte Software zu entwickeln.

Ein zentrales Thema dieses Kapitels sind Events und event-getriebene Architekturen (EDA).
Dazu gehören sowohl die Grundlagen von Events und Event-Streams, als auch weiterführende Konzepte wie Event Sourcing und Command Query Responsibility Segregation (CQRS).
Diese Architekturmuster ermöglichen eine lose Kopplung von Komponenten, eine klare Trennung von Lese- und Schreiboperationen und die Nachvollziehbarkeit von Systemzuständen und Eigenschaften, die sich besonders gut mit modernen Softwarearchitekturen kombinieren lassen.

Eng verbunden mit event-getriebenen Ansätzen ist das Konzept des Domain-Driven Design (DDD).
DDD bietet sowohl strategische als auch taktische Werkzeuge, um komplexe Domänen zu modellieren.
Themen wie Bounded Contexts, Aggregates und Event Storming liefern dabei eine klare Struktur und erleichtern die Identifikation relevanter Events, wodurch sich DDD nahtlos mit EDA, Event Sourcing und CQRS kombinieren lässt.

Auf der architektonischen Ebene werden in diesem Kapitel zudem Modulithen und hexagonale Architekturen behandelt.
Modulithen ermöglichen eine modulare, gut wartbare Struktur innerhalb einer Anwendung, während die hexagonale Architektur die Interaktion zwischen Kernlogik und äußeren Systemen sauber trennt.
Beide Konzepte ergänzen die zuvor eingeführten Patterns und tragen dazu bei, die in DDD und EDA identifizierten Strukturen konsequent umzusetzen.

Die theoretische Grundlage endet durch die Betrachtung der eingesetzten Technologien, insbesondere Kotlin als Programmiersprache und Spring Boot als Framework für die Entwicklung moderner, modularer Anwendungen.
Die Kombination dieser Technologien mit den vorgestellten Konzepten zeigt, wie sich die theoretischen Ansätze praktisch und effizient in einer Softwarelösung umsetzen lassen.

Zusammengefasst legt dieses Kapitel die Basis für die Implementierung im nächsten Abschnitt, indem es die zentralen Konzepte, Patterns und Technologien beschreibt, deren Zusammenspiel die Entwicklung flexibler, wartbarer und skalierbarer Software erleichtert.

== Events

Ein Event ist eine Nachricht, die eine bereits eingetretene Änderung beschreibt.
Es beschreibt also einen Sachverhalt, der bereits in der Vergangenheit stattgefunden hat.
Der Name eines Events besteht in der Regel aus einem Verb in der Vergangenheitsform, das ausdrückt, was geschehen ist @khononov2022[p.~264–265].

Events sind unveränderbare Fakten über vergangene Zustände oder Aktionen @stack2022[p.~8].

Events dienen dazu, Veränderungen in einem System darzustellen und anderen Systemen mitzuteilen.
Dabei gibt es mehrere Beteiligte: \
Der *Producer* erzeugt das Event und veröffentlicht es über einen Event-Mechanismus.
Dieser Mechanismus kann unterschiedliche Bezeichnungen haben.
In dieser Arbeit wird der Begriff *Event-Queue* verwendet.
Eine Queue ist dabei eine Warteschlange nach dem First-In-First-Out-Prinzip, in der Events gespeichert werden, bis sie von einem *Consumer* verarbeitet werden.
Ein Event kann von einem oder mehreren Consumern empfangen werden @stack2022[p.~8-11].

Beim Veröffentlichen eines Events muss der Producer den Consumer weder kennen noch auf dessen Verarbeitung warten.
Diese Form der Verarbeitung, bei der der Producer nicht durch den Consumer blockiert wird, wird als asynchron bezeichnet.
Wird die Event-Queue persistent gespeichert, müssen Producer und Consumer nicht gleichzeitig aktiv sein.
Dies führt zu einer zeitlichen und referenziellen Entkopplung @distributed2023[p.~69–73].

=== Event Driven Architecture

Unter einer Event-Driven Architecture (EDA) versteht man ein Architekturmuster, das auf der Verarbeitung und Weitergabe von Events basiert.
Dabei werden die Vorteile der losen Kopplung genutzt, um Systeme zu entwickeln, die weitgehend unabhängig voneinander funktionieren.
EDA ist eng mit Domain-Driven Design (DDD) verbunden, da Events in DDD eine zentrale Rolle einnehmen @khononov2022[p.~263].

Zu den Vorteilen einer EDA gehören:
- *Resilienz*: Durch die lose Kopplung der Komponenten können Fehler oder Ausfälle isoliert werden, ohne das Gesamtsystem zu beeinträchtigen.
- *Agile Entwicklung*: Unabhängig arbeitende Teams können verschiedene Komponenten parallel entwickeln. Neue Komponenten lassen sich leicht an das bestehende System anbinden.
- *Skalierbarkeit*: Komponenten können unabhängig voneinander skaliert werden, um unterschiedlichen Lastanforderungen gerecht zu werden.

Die zentralen Herausforderungen einer EDA sind:
- *Eventual Consistency*: Änderungen in einer Komponente werden nicht sofort global sichtbar.
- *Verteilte und asynchrone Workflows*: Die Koordination von Abläufen über mehrere Komponenten hinweg kann komplex sein.

@stack2022[p.~13–15] und @khononov2022[p.~263].

EDA benutzt Events auf verschiedene Weise.
Dazu gehören:
- *Event Notification*: Dabei werden Events versendet, sobald ein bestimmtes Ereignis in einem System eingetreten ist. Diese Events enthalten in der Regel nur sehr wenige Informationen über das Ereignis selbst. Meist wird lediglich mitgeteilt, dass etwas passiert ist, häufig ergänzt um relevante Identifikatoren der beteiligten Entitäten. In vielen Fällen benötigt der Empfänger keine weiteren Informationen, da es ausreichend ist, zu wissen, dass der betreffende Sachverhalt abgeschlossen wurde. Nur in speziellen Situationen muss der Empfänger zusätzliche Daten beim ursprünglichen System anfordern, um den vollständigen Kontext zu erhalten.
- *Event Sourcing*: n diesem Ansatz werden alle Änderungen des Systemzustands als eine chronologische Abfolge von Events persistiert. Der aktuelle Zustand kann jederzeit durch das erneute Abspielen dieser Events rekonstruiert werden. Dadurch entsteht ein vollständig nachvollziehbarer Verlauf aller Zustandsänderungen.
- *Event-Carried State Transfer*: Hier werden Events verwendet, die eine Statusänderung samt aller dafür notwendigen Daten enthalten. Der Empfänger kann seinen eigenen Zustand dadurch direkt und ohne zusätzliche Anfragen aktualisieren. Das Event trägt somit den gesamten fachlichen Kontext, der für den State Transfer erforderlich ist.
@stack2022[p.~4-6]

=== Event Sourcing
Beim Event Sourcing wird der Zustand einer Anwendung nicht durch das Speichern der aktuellen Daten, sondern durch eine chronologische Abfolge von Events repräsentiert.
Diese Events bilden einen unveränderbaren Event Stream. Ein Event selbst ist unveränderlich, es können lediglich neue Events an den Stream angehängt werden @vernon2013[p.~539].

Durch das Speichern des Event Streams lässt sich der aktuelle Zustand jederzeit durch das Abspielen (Replay) dieser Events rekonstruieren. Event Streams werden in der Regel in einem Event Store persistiert @vernon2013[p.~539].

Da alle Änderungen als Events gespeichert werden, steht eine vollständige Historie aller Zustandsänderungen zur Verfügung.
Es ist jederzeit nachvollziehbar, wie ein bestimmter Zustand aus dem Event Store erreicht wurde.
Dies erleichtert das Auffinden von Fehlern und die Rekonstruktion fehlerhafter Daten @vernon2013[p.~539].

In der Entwicklung einer Anwendung kann es vorkommen, dass sich der Fokus hauptsächlich auf die Verwaltung des aktuellen Datenbestands richtet.
Event Sourcing zwingt den Entwickler jedoch dazu, sich auf die fachlichen Ereignisse zu konzentrieren, die im System auftreten.
Dies fördert ein besseres Verständnis der Domäne und führt zu einer klareren Modellierung der Geschäftsprozesse @khononov2022[p.~127–132].

Zum Zeitpunkt der Entwicklung ist oft nicht absehbar, welche Anforderungen in der Zukunft auftreten werden und welche Daten dafür benötigt werden.
Durch das Speichern aller Events steht jedoch eine umfangreiche Datenbasis zur Verfügung, die für zukünftige Anforderungen genutzt werden kann.
So können beispielsweise neue Berichte oder Analysen erstellt werden, ohne dass die ursprünglichen Daten erneut erfasst werden müssen @khononov2022[p.~133].

=== Command Query Responsibility Segregation

Command Query Responsibility Segregation (CQRS) ist ein Architekturpattern, das die Verantwortlichkeiten für das Schreiben (Commands) und Lesen (Queries) von Daten trennt.

Für das Schreiben von Daten wird ein separates System verwendet, das als *Write Model* bezeichnet wird.
Das Lesen von Daten erfolgt über ein separates *Read Model*.
Beide Modelle können dabei unterschiedliche Datenstrukturen und Technologien nutzen @vernon2013[p.~138–140].

Die Trennung von Schreib- und Leseoperationen ermöglicht eine unabhängige Ausführung beider Prozesse.
Dies führt zu besserer Skalierbarkeit, da Lese- und Schreiboperationen unterschiedliche Anforderungen an die Performance haben können.
Das Write Model kann auf Konsistenz und Integrität optimiert werden, während das Read Model auf schnelle Abfragen ausgelegt ist @vernon2013[p.~140–145].

Durch die getrennte Behandlung entsteht eine eventual consistency zwischen Write Model und Read Model.
Das Write Model verarbeitet die Commands und generiert dabei Events, die den aktuellen Zustand des Systems widerspiegeln.
Das Read Model ist daher nicht zu jedem Zeitpunkt konsistent mit dem Write Model @vernon2013[p.~146–147].

CQRS und Event Sourcing lassen sich gut kombinieren, da CQRS einige Einschränkungen von Event Sourcing ausgleicht.
Ein typisches Problem beim Event Sourcing ist das Auffinden von Entitäten mit bestimmten Zuständen, da der aktuelle Zustand nicht direkt gespeichert wird.
Durch das Read Model können solche Abfragen effizient durchgeführt werden, ohne den Event Stream durchsuchen zu müssen.
Dabei fungiert der Event Stream als Write Model, dessen Events von einem separaten System konsumiert werden, das als Read Model dient @vernon2013[p.~140–145].

== Domain Driven Design

=== Taktisches und Strategisches Design

=== Bounded Context

=== Aggregates

=== Domain Events

=== Event Storming

== Modulith

=== Hexagonale Architektur

= Implementierung

== Erkunden der Domain

== Architektur

== Aggregate Class

== Event Store

== Event System

== Domain Modul

== View Modul

== Spring Modulith

= Evaluierung

#bibliography("literatur.bib")


#pagebreak()
