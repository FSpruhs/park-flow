#set page(
    width: 21cm,
    height: 29.7cm,
    margin: 2.5cm,
    numbering: none,
)

#show link: underline

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

    "CQRS", "Command Query Responsibility Segregation",
    "DDD", "Domain driven design",
    "EDA", "Event Driven Architecture",
    "ES", "Event Sourcing",
    "R2DBC", "Reactive Relational Database Connectivity",
    "REST", "Representational State Transfer",

)

#pagebreak()

= Einleitung

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

= Verwandte Arbeiten

Die Grundlage für DDD wurde 2003 durch Eric Evans und sein Werk "Domain-Driven Design: Tackling Complexity in the Heart of Software" geschaffen @evans2003.
Evans beschreibt darin einen umfassenden, systematischen Ansatz, um komplexe fachliche Domänen zu analysieren, zu strukturieren und in Software umzusetzen.
Sein Buch bildet bis heute die theoretische Basis von DDD.

Im Jahr 2013 veröffentlichte Vaughn Vernon mit "Implementing Domain-Driven Design" ein praxisorientierteres Werk, das konkrete Vorgehensweisen und Implementierungsstrategien für die Anwendung von DDD in realen Projekten beschreibt @vernon2013.
Beide Werke bilden die zentrale Grundlage für die in dieser Arbeit verwendeten DDD-Konzepte.
#footnote[In der DDD-Community werden diese Bücher aufgrund der Farbgestaltung ihrer Einbände häufig als „Blue Book“ (Evans) und „Red Book“ (Vernon) bezeichnet.]

2022 hat Michael Stack das Buch Event-Driven Architecture in Golang veröffentlicht @stack2022.
Darin beschreibt er eine umfassende Einführung in ereignisgetriebene Architekturen (EDA) und deren praktische Umsetzung.
Als Programmiersprache verwendet er Go (Golang) und Entwickelt damit eine Beispielanwendung als Microservice-Architektur.
Ich werde in dieser Arbeit untersuchen, inwiefern sich die Konzepte aus Stacks Buch auf Kotlin und Spring Boot übertragen lassen und was sich bei der Umsetzung in einem modularen Monolithen ändert.

= Theoretische Grundlagen

In diesem Kapitel werden die theoretischen Grundlagen vorgestellt, die für die Umsetzung der in dieser Arbeit entwickelten Softwarelösung relevant sind.
Ziel ist es, ein solides Verständnis der zentralen Konzepte und Technologien zu vermitteln, die im anschließenden Kapitel gemeinsam in einer konkreten Anwendung praktisch umgesetzt werden.

Die Wahl dieser Konzepte basiert auf der Notwendigkeit, komplexe Software domänenspezifisch, wartbar und erweiterbar zu gestalten. Insbesondere in modernen Anwendungen mit hohen Anforderungen an Skalierbarkeit und Fehlertoleranz bieten die kombinierten Ansätze aus EDA, DDD und modularen Architekturen deutliche Vorteile.
Dabei wird aufgezeigt, wie die einzelnen Ansätze ineinandergreifen und sich gegenseitig ergänzen, um flexible, skalierbare und gut strukturierte Software zu entwickeln.

Ein zentrales Thema dieses Kapitels sind Events und event-getriebene Architekturen (EDA).
Hierzu gehören sowohl die Grundlagen von Events und Event-Streams als auch weiterführende Konzepte wie Event Sourcing und Command Query Responsibility Segregation (CQRS).
Diese Architekturmuster ermöglichen eine lose Kopplung von Komponenten, eine klare Trennung von Lese- und Schreiboperationen sowie die Nachvollziehbarkeit von Systemzuständen.
Diese Eigenschaften eignen sich besonders für moderne Softwarearchitekturen.

Eng verbunden mit event-getriebenen Ansätzen ist das Konzept des Domain-Driven Design (DDD).
DDD bietet sowohl strategische als auch taktische Werkzeuge, um komplexe Domänen zu modellieren.
Konzepte wie Bounded Contexts, Aggregates und Event Storming liefern eine klare Struktur und erleichtern die Identifikation relevanter Events.
Dadurch lässt sich DDD nahtlos mit EDA, Event Sourcing und CQRS kombinieren.

Auf der architektonischen Ebene werden zudem Modulithen und hexagonale Architekturen betrachtet.
Modulithen ermöglichen eine modulare und gut wartbare Struktur innerhalb einer Anwendung, während die hexagonale Architektur die Interaktion zwischen Kernlogik und externen Systemen sauber trennt.
Beide Konzepte ergänzen die zuvor eingeführten Patterns und tragen dazu bei, die in DDD und EDA identifizierten Strukturen konsequent umzusetzen.

Abschließend werden die eingesetzten Technologien betrachtet, insbesondere Kotlin als Programmiersprache und Spring Boot als Framework für die Entwicklung moderner, modularer Anwendungen.
Die Kombination dieser Technologien mit den vorgestellten Konzepten zeigt, wie sich die theoretischen Ansätze effizient in einer Softwarelösung umsetzen lassen.

Zusammenfassend legt dieses Kapitel die Grundlage für die Implementierung im folgenden Abschnitt, indem es die zentralen Konzepte, Patterns und Technologien beschreibt und deren Zusammenspiel aufzeigt.
Auf dieser Basis können flexible, wartbare und skalierbare Softwarelösungen entwickelt werden.

== Events

Ein *Event* ist eine Nachricht, die eine bereits eingetretene Änderung beschreibt.
Es handelt sich somit um einen Sachverhalt, der in der Vergangenheit stattgefunden hat.
Der Name eines Events besteht in der Regel aus einem Verb in der Vergangenheitsform, das ausdrückt, was geschehen ist @khononov2022[p.~264–265].

Events sind unveränderbare Fakten über vergangene Zustände oder Aktionen @stack2022[p.~8].
Sie dienen dazu, Veränderungen in einem System zu dokumentieren und anderen Systemen mitzuteilen.

Dabei gibt es mehrere Beteiligte: \
Der *Producer* erzeugt das Event und veröffentlicht es über einen *Event-Queue*#footnote[Auch bekannt als Event-Bus, Publisher oder Broker].
In dieser Arbeit wird der Begriff Event-Queue verwendet.
Eine Queue ist dabei eine Warteschlange nach dem First-In-First-Out-Prinzip, in der Events gespeichert werden, bis sie von einem *Consumer* verarbeitet werden.
Ein Event kann von einem oder mehreren Consumern empfangen werden @stack2022[p.~8-11].

Beim Veröffentlichen eines Events muss der Producer den Consumer weder kennen noch auf dessen Verarbeitung warten.
Diese Form der Verarbeitung, bei der der Producer nicht durch den Consumer blockiert wird, wird als asynchron bezeichnet.
Wird die Event-Queue persistent gespeichert, müssen Producer und Consumer nicht gleichzeitig aktiv sein.
Dies führt zu einer zeitlichen und referenziellen Entkopplung, die die Flexibilität und Skalierbarkeit des Systems erhöht @distributed2023[p.~69–73].

=== Event Driven Architecture

Unter einer *Event-Driven Architecture (EDA)* versteht man ein Architekturmuster, das auf der Verarbeitung und Weitergabe von Events basiert.
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
- *Event Sourcing*: In diesem Ansatz werden alle Änderungen des Systemzustands als eine chronologische Abfolge von Events persistiert. Der aktuelle Zustand kann jederzeit durch das erneute Abspielen dieser Events rekonstruiert werden. Dadurch entsteht ein vollständig nachvollziehbarer Verlauf aller Zustandsänderungen.
- *Event-Carried State Transfer*: Hier werden Events verwendet, die eine Statusänderung samt aller dafür notwendigen Daten enthalten. Der Empfänger kann seinen eigenen Zustand dadurch direkt und ohne zusätzliche Anfragen aktualisieren. Das Event trägt somit den gesamten fachlichen Kontext, der für den State Transfer erforderlich ist.
@stack2022[p.~4-6]

Diese Ansätze zeigen, wie EDA die Vorteile von losgekoppelten, flexiblen und skalierbaren Systemen praktisch umsetzt, gleichzeitig aber auch die Komplexität in verteilten Systemen adressiert.

=== Event Sourcing

Beim *Event Sourcing* wird der Zustand einer Anwendung nicht durch das Speichern der aktuellen Daten, sondern durch eine chronologische Abfolge von Events repräsentiert.
Diese Events bilden einen unveränderbaren Event Stream. Ein Event selbst ist unveränderlich, es können lediglich neue Events an den Stream angehängt werden @vernon2013[p.~539].

Durch das Speichern des Event Streams lässt sich der aktuelle Zustand jederzeit durch das Abspielen (Replay) dieser Events rekonstruieren.
Event Streams werden in der Regel in einem Event Store persistiert, der die Events zuverlässig speichert und für die Rekonstruktion bereitstellt @vernon2013[p.~539].

Da alle Änderungen als Events gespeichert werden, steht eine vollständige Historie aller Zustandsänderungen zur Verfügung.
Es ist jederzeit nachvollziehbar, wie ein bestimmter Zustand aus dem Event Store erreicht wurde.
Dies erleichtert das Auffinden von Fehlern und die Rekonstruktion fehlerhafter Daten @vernon2013[p.~539].

Event Sourcing verschiebt den Fokus der Anwendungsentwicklung von der bloßen Verwaltung des aktuellen Datenbestands hin zur Modellierung fachlicher Ereignisse, die im System auftreten.
Dies fördert ein tieferes Verständnis der Domäne und führt zu einer klareren Modellierung der Geschäftsprozesse @khononov2022[p.~127–132].

Ein weiterer Vorteil ergibt sich aus der langfristigen Flexibilität der Datenbasis.
Zum Zeitpunkt der Entwicklung ist oft nicht absehbar, welche Anforderungen in der Zukunft auftreten werden und welche Daten dafür benötigt werden.
Durch das Speichern aller Events steht jedoch eine umfangreiche Datenbasis zur Verfügung, die für zukünftige Anforderungen genutzt werden kann.
So können beispielsweise neue Berichte oder Analysen erstellt werden, ohne dass die ursprünglichen Daten erneut erfasst werden müssen @khononov2022[p.~133].

=== Command Query Responsibility Segregation

Command Query Responsibility Segregation (CQRS) ist ein Architekturpattern, das die Verantwortlichkeiten für das Schreiben (Commands) und Lesen (Queries) von Daten strikt trennt.
Dadurch werden fachliche Aktionen klar von Abfragen getrennt, was sowohl die Skalierbarkeit als auch das Verständnis der Domäne fördert.

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
Dabei fungiert der Event Stream als Write Model, dessen Events von einem separaten System konsumiert werden, das als Read Model dient.
Auf diese Weise entsteht eine saubere Trennung von Fachlogik und Datenzugriff, die sowohl die Domäne klarer abbildet als auch die Vorteile von Event Sourcing optimal nutzt @vernon2013[p.~140–145].

== Domain Driven Design

Domain Driven Design (DDD) ist eine Methodik zur Entwicklung von Software, die ein hochwertiges, fachlich getreues Modell der zugrunde liegenden Domäne ermöglicht.
Ziel ist es, die Software so zu gestalten, dass sie die fachlichen Anforderungen der Domäne bestmöglich abbildet @vernon2013[p.~1].

Eine zentrale Herausforderung bei der Entwicklung komplexer Software liegt weniger in den technischen Aspekten, sondern vielmehr in der Beherrschung der fachlichen Komplexität.
DDD adressiert diese Herausforderung, indem es den Fokus konsequent auf die Domäne legt und die Gestaltung von Modellen in den Vordergrund stellt, die die realen Abläufe und Sachverhalte der Domäne widerspiegeln @evans2003[preface xxi].

Durch diese Fokussierung auf die Domäne können Softwarelösungen entwickelt werden, die enger an der Realität der Fachprozesse orientiert sind.

=== Taktisches und Strategisches Design

DDD lässt sich in zwei Hauptbereiche unterteilen, dem *strategischen* und dem *taktischen* Design.

Das strategische Design beschäftigt sich mit der Analyse und Strukturierung der Domäne auf hoher Ebene.
Ziel ist es, herauszuarbeiten, welche Software entwickelt werden soll und warum, und wie die Domäne sinnvoll in fachliche Teilbereiche gegliedert werden kann.

Mit dem strategischen Design soll die Komplexität der Domäne beherrschbar gemacht werden.
Ein einzelnes Modell reicht oft nicht aus, um alle Aspekte einer komplexen Domäne abzubilden.
Das System muss in mehrere besser handhabbare Teile zerlegt werden ohne dabei die Vorteile der Integration zu verlieren @evans2003[p.~328].

Dafür stehen im strategischen Design verschiedene Werkzeuge und Konzepte zur Verfügung, die dabei helfen, die Domäne zu verstehen, Verantwortlichkeiten abzugrenzen und Zusammenhänge zu visualisieren.
Ein zentraler Aspekt ist die Kommunikation zwischen allen Beteiligten, um ein gemeinsames Verständnis der Domäne sicherzustellen.
Dieses gemeinsame Wissen dient als Grundlage für Designentscheidungen auf hoher Ebene @khononov2022[p.~26–27].

In diesem Kapitel werde ich die Strategischen Begriffe, Subdomain, Bounded Context und Ubiquitous Language vorstellen die ich auch bei der Implementierung verwenden werde.

Das taktische Design setzt eine Ebene darunter an und beschäftigt sich mit der konkreten Umsetzung der Softwarekomponenten.
Es beschreibt, wie das im strategischen Design entwickelte Domänenmodell technisch realisiert wird @khononov2022[p.~89].
In diesem Kapitel werde ich die Taktischen Begriffe, Entities, Value Objects, Aggregates, Domain Events und Modules

=== Subdomain

Als Domain wird alles bezeichnet, womit sich eine Organisation #footnote[z.B. Unternehmen oder öffentliche Institutionen] beschäftigt und in welchem fachlichen Kontext sie tätig ist @vernon2013[p.~43].
Die Domain beschreibt somit den fachlichen Kontext, in dem die Software operiert, und umfasst die Geschäftsprozesse, Regeln und Anforderungen, die für die Organisation relevant sind.

Damit die Ziele der Domain erreicht werden können, wird sie in mehrere Subdomains unterteilt.
Subdomains lassen sich in drei Kategorien einordnen:

- *Core Subdomain*: Die Core Subdomain stellt die Haupttätigkeit der Organisation dar. Sie definiert, wodurch sich die Organisation von ihren Wettbewerbern abhebt, und repräsentiert das, was die Organisation besonders macht. Die Hauptentwicklung sollte sich auf die Core Subdomain konzentrieren, da hier der größte Mehrwert liegt.
- *Supporting Subdomain*: Supporting Subdomains unterstützen die Core Subdomain dabei, ihre Ziele zu erreichen, bilden aber nicht das Hauptbetätigungsfeld der Organisation. Sie sind für den Gesamterfolg wichtig, liefern jedoch keinen direkten Wettbewerbsvorteil.
- *Generic Subdomain*: Generic Subdomains sind allgemeine, standardisierte Bereiche, die viele Organisationen ebenfalls besitzen. Sie sind nicht spezifisch für die Organisation und bieten keinen Wettbewerbsvorteil. Solche Domains können häufig durch Standardlösungen oder Drittanbieter abgedeckt werden.
@khononov2022[p.~30-33]

Subdomains helfen nicht nur, die Komplexität der Softwareentwicklung zu reduzieren, sondern fördern auch die Fokussierung auf die fachlich relevanten Bereiche.
Sie schaffen klare Abgrenzungen, die es erleichtern, Verantwortlichkeiten zu definieren, Zuständigkeiten nachvollziehbar zu machen und langfristig stabile Software zu entwickeln.

=== Ubiquitous Language

Ein zentrales Element von DDD ist die Ubiquitous Language („allgegenwärtige Sprache“).
Sie besagt, dass alle Beteiligten #footnote[z.B. Entwickler, Fachexperten, Architekten und weitere Stakeholder] eine gemeinsame Sprache verwenden, die sich aus der Domain ableitet.

Die Ubiquitous Language dient mehreren Zwecken:

- Sie erleichtert die Verständigung zwischen allen Beteiligten und stellt sicher, dass fachliche Anforderungen klar, präzise und einheitlich kommuniziert werden können.
- Sie verhindert Übersetzungen zwischen unterschiedlichen Begrifflichkeiten, die in der Softwareentwicklung häufig zu Missverständnissen führen. In Projekten, in denen Entwickler, Product Owner und Fachexperten unterschiedliche Worte für dasselbe Konzept verwenden, entstehen oft Fehler, unklare Anforderungen oder ungenaue Implementierungen.
- Sie bindet technische Begriffe nur insoweit ein, wie sie die fachliche Sprache unterstützen, und verhindert, dass technisches Vokabular die fachliche Sicht überlagert.
@khononov2022[p.~50–51]

Darüber hinaus ist die Ubiquitous Language ein entscheidendes Werkzeug, um ein konsistentes Domänenmodell zu entwickeln.
Jedes Modell-Element sollte mit einem Begriff aus der Ubiquitous Language benannt werden.
Auf diese Weise kann präzise beschrieben werden, was entwickelt werden soll, und alle Beteiligten verstehen dasselbe Konzept auf dieselbe Weise.

=== Bounded Context

Ein zentrales Ziel von DDD ist es, verschiedene Modelle zu entwickeln, die unterschiedliche Aspekte der Domain abbilden und dabei helfen, das reale System besser zu verstehen.
Jedes Modell soll nur die Elemente enthalten, die für seinen Zweck erforderlich sind, während unnötige Details bewusst ausgeklammert werden.
Auf diese Weise wird die Komplexität des Modells auf ein Minimum reduziert @khononov2022[p.~53–54].

In der Praxis kann dasselbe fachliche Konzept in unterschiedlichen Teilen der Organisation unterschiedliche Rollen einnehmen.
Wird versucht, ein einziges Modell für alle Anwendungsfälle zu verwenden, entsteht schnell ein sehr großes, komplexes Modell, das schwer verständlich und fehleranfällig ist.
DDD adressiert diese Herausforderung, indem es Modelle aufteilt und klar abgegrenzte Bereiche definiert.

Ein Bounded Context bezeichnet einen abgegrenzten Bereich, in dem ein Modell gültig, konsistent und eindeutig definiert ist.
Das gleiche fachliche Modell kann somit in mehreren Bounded Contexts existieren, unterscheidet sich dort jedoch in Bedeutung und Verwendung @khononov2022[p.~63–64].

Während Subdomains fachliche Bereiche der Organisation beschreiben, definieren Bounded Contexts die technischen und organisatorischen Grenzen, innerhalb derer ein Modell konsistent angewendet wird.
Sie dienen als Schnittstellen zwischen verschiedenen Modellen und verhindern, dass Konzepte außerhalb ihres Geltungsbereichs falsch interpretiert oder vermischt werden.

Die Ubiquitous Language wird innerhalb eines Bounded Contexts festgelegt. Gleichlautende Begriffe müssen dabei nicht zwingend die gleiche Bedeutung in anderen Contexts haben.
So kann ein und dasselbe Wort in verschiedenen Bounded Contexts unterschiedliche Bedeutungen besitzen @khononov2022[p.~65].

Durch die Kombination von Subdomains, Bounded Contexts und Ubiquitous Language wird die Komplexität der Domäne beherrschbar.
Jedes Modell bleibt auf seinen Zweck fokussiert, ist leichter verständlich und langlebiger, während gleichzeitig die konsistente Kommunikation zwischen allen Beteiligten gesichert wird.

=== Entities

Domain-Konzepte, die eine eigene Identität besitzen und sich dadurch eindeutig von anderen Objekten unterscheiden lassen, werden als Entities modelliert.
Eine Entity wird nicht über ihre Attribute definiert, sondern über eine stabile, eindeutige Identität, die sie innerhalb der Domäne unverwechselbar macht.
Entities repräsentieren typischerweise fachliche Konzepte, die über einen längeren Zeitraum bestehen, sich weiterentwickeln und oft in verschiedenen Bounded Contexts relevant sind.@vernon2013[p.~171-172].

Wesentlich ist zudem, dass Entities mehr sind als reine Datencontainer.
Sie verkörpern fachliches Verhalten, enthalten Regeln, Invarianten und Logik und stellen so sicher, dass ihr Zustand jederzeit konsistent bleibt.
Durch diese Kapselung von Verhalten innerhalb der Entity wird die Langlebigkeit und Robustheit des Systems erhöht, da fachliche Regeln zentral an einer Stelle umgesetzt werden und nicht über verstreute Services verteilt sind.

=== Value Objects

Value Objects sind Domain-Konzepte, die nicht über eine eigene Identität definiert werden, sondern ausschließlich über ihre Attributwerte.
Zwei Value Objects gelten als gleich, wenn alle ihre relevanten Eigenschaften übereinstimmen.
Ihre Identität ergibt sich, im Gegensatz zu Entities, also vollständig aus ihren Werten, deren Identität unabhängig vom aktuellen Zustand bleibt @vernon2013[p.~219-220].

Value Objects repräsentieren häufig kleine, unveränderliche Werte oder Konzepte, die in der Domäne eine klare fachliche Bedeutung haben.
Sie sollen präzise ausdrücken, was ein bestimmter Wert fachlich bedeutet, und stellen sicher, dass dieser Wert nur in gültigen Kombinationen vorkommt.

Ein wesentliches Merkmal von Value Objects ist ihre Unveränderlichkeit (Immutability).
Statt ihren Zustand zu verändern, wird bei einer Änderung ein neues Objekt erzeugt.
Dadurch bleiben Value Objects jederzeit konsistent und können gefahrlos gemeinsam verwendet, verglichen oder wiederverwendet werden.

Die Verwendung von Value Objects fördert sauberen Code, da fachliche Regeln zentral in der Konstruktion des Objekts umgesetzt werden können.

=== Aggregates

Ein Aggregate ist eine besondere Form der Entity.
Es besitzt ebenfalls eine eigene Identität, ist langlebig und repräsentiert ein fachlich bedeutungsvolles Konzept innerhalb der Domäne.
Im Gegensatz zu einzelnen Entities besteht ein Aggregate jedoch aus mehreren miteinander verbundenen Entities und Value Objects, die gemeinsam eine Konsistenzeinheit bilden @khononov2022[p.~112].

Die zentrale Aufgabe eines Aggregates besteht darin, die Konsistenzregeln und Invarianten aller zugehörigen Objekte sicherzustellen.
Um diese Aufgabe zuverlässig erfüllen zu können, enthält das Aggregate die wesentliche fachliche Logik (Business Logic), die für die Integrität seines inneren Zustands verantwortlich ist @khononov2022[p.~113].

Ein Aggregate definiert eine klare fachliche und transaktionale Grenze, innerhalb derer alle Änderungen atomar und konsistent durchgeführt werden müssen.
Von außen darf ein Aggregate nur über seine Aggregate Root verändert werden – die Entity, die das Aggregate repräsentiert und dessen einzige Zugriffsstelle ist.
Auf diese Weise wird verhindert, dass Außenstehende direkt auf interne Entities oder Value Objects zugreifen und dadurch Konsistenzregeln verletzen @khononov2022[p.~115].

Aggregates stehen zueinander in einem losen Kopplungsverhältnis.
Sie dürfen einander weder direkt aufrufen noch auf internem Wege referenzieren @khononov2022[p.~117].
Durch diese Entkopplung wird die Modularität erhöht und die Komplexität des Gesamtsystems beherrschbar gehalten.

Aggregates gehören zu den zentralen Bausteinen von DDD.
Sie spielen eine grundlegende Rolle, weil sie:

- *Konsistenz garantieren*: Die innerhalb eines Aggregates definierten Regeln gelten immer und können nicht umgangen werden.
- *Komplexität kontrollieren*: Statt große, schwer beherrschbare Objektstrukturen zu modellieren, zerschneidet man die Domäne in kleinere, klar abgegrenzte Einheiten.
- *Transaktionen begrenzen*: Jedes Aggregate bildet den Rahmen für eine Transaktion. Dies verhindert ineffiziente oder gefährliche verteilte Transaktionen über mehrere Aggregate hinweg.
- *Skalierbarkeit unterstützen*: Durch die Entkopplung der Aggregate können Systeme leichter horizontal skaliert werden.
- *Einen stabilen Architekturrahmen schaffen*: Aggregates wirken als zentrale Strukturierungselemente, an denen sich Services, Anwendungslogik und Persistenz orientieren.

Die Modellierung sinnvoller Aggregate ist eine der anspruchsvollsten Aufgaben im DDD @vernon2013[p.~347]. Typische Herausforderungen sind:

- *Das richtige Granularitätsniveau finden*: Ein Aggregate darf nicht zu groß sein, da sonst Transaktionen schwerfällig werden. Ist es zu klein, gehen Konsistenzregeln verloren oder müssen außerhalb des Aggregats kontrolliert werden.
- *Konsistenz vs. Performance ausbalancieren*: Zu viele invariantenbedingte Abhängigkeiten führen zu unnötig großen Aggregaten. Zu wenige führen zu verteilten, schwer kontrollierbaren Geschäftsregeln.

=== Domain Events

Domain Events sind Ereignisse, die eine bedeutsame Zustandsänderung innerhalb der Domäne repräsentieren.
Sie spiegeln fachliche Ereignisse wider, die für die Geschäftsprozesse relevant sind, und dienen als Kommunikationsmittel zwischen verschiedenen Teilen des Systems @vernon2013[p.~285].

Domain Events spielen insbesondere in Event-Driven Architekturen (EDA) eine zentrale Rolle.
Wie im vorherigen Abschnitt beschrieben, dürfen Aggregates nicht direkt auf andere Aggregates zugreifen.
Wenn jedoch eine Zustandsänderung eines Aggregates für andere Aggregates relevant ist, wird diese Änderung über ein Domain Event mitgeteilt @khononov2022[p.~119].

Alle Aggregates, die an dieser Zustandsänderung interessiert sind, können das Event empfangen und darauf reagieren.
Auf diese Weise entsteht eine lose Kopplung zwischen den Aggregates, und Änderungen können asynchron verarbeitet werden.

Domain Events haben zudem eine fachliche Bedeutung über die reine technische Umsetzung hinaus:

- Sie dokumentieren, dass etwas tatsächlich geschehen ist, und machen Zustandsänderungen nachvollziehbar.
- Sie fördern die Nachvollziehbarkeit und Transparenz innerhalb der Domäne, da jedes Event einen konkreten fachlichen Sachverhalt beschreibt.
- Sie erleichtern die Kommunikation zwischen Entwicklern und Fachexperten, da sie direkt auf die Ubiquitous Language der Domäne abgebildet werden können.

Durch diese Eigenschaften tragen Domain Events wesentlich zur Skalierbarkeit, Flexibilität und Wartbarkeit von Systemen bei, da sie die Domänenlogik klar strukturieren und gleichzeitig eine Erweiterbarkeit ermöglichen, ohne bestehende Aggregate direkt zu beeinflussen.

=== Modules

Ein Module ist eine logische Zusammenfassung eng verwandter Domänenelemente innerhalb eines Bounded Contexts.
Innerhalb eines Moduls herrscht hohe Kohäsion.
Die enthaltenen Klassen, Konzepte und Regeln stehen in einem klaren fachlichen Zusammenhang und tragen gemeinsam zur Lösung einer spezifischen Teilaufgabe der Domäne bei.

Zwischen Modulen sollte hingegen eine möglichst geringe Kopplung bestehen.
Diese Trennung fördert die Verständlichkeit, Wartbarkeit und Weiterentwicklung des Systems, da Änderungen innerhalb eines Moduls keine oder nur geringe Auswirkungen auf andere Module haben.

Wichtig ist, dass Module fachlich und nicht technisch geschnitten werden.
Sie orientieren sich also an der Problem- und Begriffswelt der Domäne und nicht an technischen Strukturen @vernon2013[p.~333–334].

Durch die Verwendung von Modulen können komplexe Systeme wie Modulithen klar strukturiert werden. Jedes Modul bildet dabei eine in sich geschlossene Einheit, die sowohl die fachliche Kohärenz als auch die organisatorische Trennung unterstützt.

=== Event Storming

Event Storming ist ein Werkzeug, um das Fachwissen einer Domäne sichtbar zu machen, zu strukturieren und im Team zu verbreiten.
Es handelt sich um einen kollaborativen Workshop-Ansatz, bei dem eine heterogene Gruppe, bestehend aus Fachexperten, Entwicklern und weiteren Stakeholdern, gemeinsam die Geschäftsprozesse und Abläufe einer Domäne modelliert.

Den Ausgangspunkt bilden dabei die Domain Events, die als zentrale Orientierungspunkte dienen.
Sie beschreiben bedeutende fachliche Ereignisse und helfen, den Ablauf und die Zustandsänderungen innerhalb der Domäne nachvollziehbar darzustellen.

Ausgehend von diesen Domain Events werden im weiteren Verlauf des Workshops zusätzliche Elemente identifiziert, darunter Commands, Aggregates, Read Models sowie externe Systeme.
Diese Elemente werden in Beziehung zueinander gesetzt, um ein umfassendes Verständnis der Domäne und ihrer Interaktionen zu entwickeln.

Die Modellierung erfolgt typischerweise an einem großen Whiteboard, auf dem die verschiedenen Bestandteile mithilfe farbcodierter Post-its visualisiert werden @khononov2022[p.~235–236].

Event Storming fördert die gemeinsame Sprache (Ubiquitous Language), weil alle Beteiligten dieselben Begriffe verwenden, um die Domäne zu beschreiben.
Zudem erleichtert es das Erkennen von Engpässen, Abhängigkeiten und komplexen Abläufen frühzeitig, bevor diese in Code umgesetzt werden.
Durch diese Sichtbarkeit der Prozesse lassen sich Softwaremodelle entwickeln, die stärker an der Realität der Domäne ausgerichtet sind.

== Architektur

Bei der Umsetzung von Softwarelösungen stehen verschiedene Architekturmuster zur Verfügung, die jeweils unterschiedliche Herausforderungen adressieren und eigene Stärken besitzen.
In dieser Arbeit werde ich mich auf den Modulithen konzentrieren.
Dabei handelt es sich um eine Architektur, die die Vorteile von Monolithen und Microservices miteinander kombiniert.

Um die Bedeutung des Modulithen besser einordnen zu können, werden zunächst die beiden grundlegenden Architekturstile Monolith und Microservices vorgestellt.
Beide bilden die konzeptionelle Grundlage für das Verständnis des Modulithen.

Zusätzlich wird in diesem Kapitel die hexagonale Architektur betrachtet.
Sie spielt eine wichtige Rolle für die interne Strukturierung von Modulen und Bounded Contexts.
Die hexagonale Architektur stellt sicher, dass Domänenlogik von technischen Details getrennt wird und erleichtert damit langfristige Wartbarkeit, Testbarkeit und eine klare Ausrichtung an der fachlichen Domäne.

=== Monolith

Ein Monolith ist eine Softwareanwendung, die als eine einzige, zusammenhängende Einheit entwickelt, bereitgestellt und betrieben wird @köhler2025[p.~327].
Alle Bestandteile der Anwendung, wie Benutzeroberfläche, Geschäftslogik und Persistenz — laufen typischerweise in einem einzigen Prozess und werden gemeinsam versioniert und ausgerollt.

Monolithen zeichnen sich durch ihre strukturelle Einfachheit aus. Da alle Komponenten innerhalb derselben Anwendung laufen, entfällt die Komplexität verteilter Systeme.
Dies macht sie insbesondere in frühen Projektphasen oder für kleinere Teams attraktiv.

Vorteile von Monolithen sind:

- *Einfache Entwicklung und Bereitstellung*: Da alle Komponenten in einer einzigen Anwendung enthalten sind, ist die Entwicklung und das Deployment vergleichsweise unkompliziert.
- *Geringer Overhead*: Monolithen benötigen keine komplexe Infrastruktur für die Kommunikation zwischen verschiedenen Diensten, was den Overhead reduziert.
- *Einfache Tests*: Integrationstests können leichter durchgeführt werden, da alle Komponenten in einer einzigen Anwendung laufen.

Mit zunehmender Größe eines Systems treten jedoch auch deutliche Nachteile zutage, insbesondere, wenn viele Teams gleichzeitig am selben Code arbeiten oder die Domäne komplex wird.

Ein Monolith ohne klare modulare oder fachliche Struktur tendiert dazu, sich im Laufe der Zeit zu einem sogenannten Big Ball of Mud zu entwickeln @vernon2017[p.~16].
Typische Symptome sind:

- Komponenten referenzieren sich gegenseitig ohne klare Regeln.
- Änderungen an einer Stelle führen zu unerwarteten Seiteneffekten an anderen Stellen.
- Die Codebasis wird immer schwerer zu verstehen, weiterzuentwickeln und zu testen.
- Die Entwicklungsgeschwindigkeit nimmt ab, da jede Anpassung potenziell das gesamte System beeinflusst.

=== Microservices

Microservices sind ein Architekturmuster, bei dem eine Anwendung aus einer Sammlung kleiner, unabhängiger Dienste besteht.
Jeder Dienst ist für eine klar abgegrenzte Funktionalität verantwortlich und kommuniziert über wohldefinierte Schnittstellen mit anderen Diensten @distributed2023[p.~65–66].

Microservices passen konzeptionell gut zu den Prinzipien des Domain-Driven Design.
Häufig bildet ein Microservice einen oder mehrere Bounded Contexts ab und kapselt dadurch einen geschlossenen fachlichen Verantwortungsbereich.
Zudem kann jeder Service genau die Technologien, Datenbanken und Programmiersprachen verwenden, die für seine spezifische Aufgabe am besten geeignet sind @khononov2022[p.~255–256].

Microservices bieten mehrere Vorteile:

- Unabhängiges Deployment: Jeder Dienst kann getrennt entwickelt, veröffentlicht und aktualisiert werden, ohne dass andere Dienste neu ausgerollt werden müssen.
- Skalierbarkeit: Dienste können individuell horizontal skaliert werden — dort, wo Last anfällt.
- Höhere Fehlertoleranz: Der Ausfall eines einzelnen Dienstes muss nicht zwingend zu einem Ausfall der gesamten Anwendung führen, sofern geeignete Mechanismen eingesetzt werden.

Diese Vorteile gehen jedoch mit erheblichen Herausforderungen einher.
Da eine Anwendung nicht mehr in einem einzelnen Prozess läuft, steigt die Komplexität der Gesamtarchitektur wesentlich an.
Dienste müssen orchestriert, überwacht und abgesichert werden. Die Kommunikation erfolgt zwangsläufig über ein Netzwerk, was neue Fehlerquellen eröffnet.

Damit einhergehen weitere Herausforderungen wie der Netzwerklatenz, der Fehlertoleranz, der Sicherheit und der Datenkonsistenz @distributed2023[p.~53].

Microservices ermöglichen also hohe Flexibilität und Skalierbarkeit, erfordern jedoch gleichzeitig eine deutlich komplexere Infrastruktur.
Ohne klare Domänengrenzen, ein durchdachtes Deployment-Konzept und ausgereifte Betriebsprozesse führt ein Microservice-System schnell zu unnötigem Overhead und sinkender Produktivität.

=== Modulith

Ein Modulith ist ein Architekturmuster, das die Vorteile von Monolithen und Microservices vereint.
Die Anwendung wird als eine einzige, gemeinsam deployte Einheit bereitgestellt, ist intern jedoch in klar abgegrenzte, fachlich motivierte Module strukturiert @stack2022[p.~41].
Diese Module habe eine konkrete Schnittstelle und kommunizieren untereinander über diese Schnittstellen.
Diese Module bilden eigenständige Verantwortungsbereiche ab und orientieren sich häufig an Bounded Contexts aus dem Domain-Driven Design.

Vom klassischen Monolithen übernimmt der Modulith vor allem die einfache Bereitstellung und den geringen infrastrukturellen Overhead.
Durch die interne Modularisierung entsteht jedoch eine klare, disziplinierte Struktur, die die Wartbarkeit, Erweiterbarkeit und langfristige Stabilität der Anwendung deutlich verbessert.
Zudem spiegeln die Module in vielen Fällen dieselben Grenzen wider, die in einer Microservice-Landschaft zu eigenständigen Diensten führen würden.

Dabei gehen einige Nachteile von Microservices verloren, da keine komplexe Infrastruktur für die Kommunikation zwischen den Diensten erforderlich ist.
Die Module kommunizieren innerhalb des gleichen Prozesses, was die Latenz reduziert und die Fehlertoleranz erhöht.

Allerdings gehen dabei auch zentrale Vorteile von Microservices verloren.
Module können nicht unabhängig voneinander skaliert oder separat deployt werden, da die Anwendung stets als Ganzes bereitgestellt wird.
Für Szenarien, in denen unterschiedliche Teile des Systems stark unterschiedliche Lastprofile haben oder unabhängig weiterentwickelt werden müssen, kann dies einschränkend sein.

Für viele Softwareprojekte ist der Modulith dennoch eine besonders attraktive Lösung.
Er eignet sich vor allem dann, wenn die Komplexität moderat ist oder wenn die Anforderungen noch nicht so stabil sind, dass eine verteilte Systemlandschaft gerechtfertigt wäre.
Da ein Modulith seinen internen Aufbau entlang klarer Bounded Contexts strukturiert, kann er mit vergleichsweise geringem Aufwand in eine Microservice-Architektur überführt werden.
Dadurch ermöglicht er eine schrittweise Evolution.
Teams können zunächst die fachliche Domäne sauber modellieren, ohne frühzeitig mit der betrieblichen Komplexität einer Microservice-Infrastruktur belastet zu werden.

In Abbildung @modulith-diagram ist der Unterschied zwischen Monolithen, Modulithen und Microservices dargestellt.
Bei a) ist ein Monoloth dargestellt, es handelt sich um eine einzige Anwendung ohne klare Strukturierung.
Bei c) sind Microservices dargestellt, die als unabhängige Dienste agieren und über ein Netzwerk kommunizieren.
Die Komplexität wird hierbei innerhalb eines Services abgebildet.
In b) ist ein Modulith dargestellt, der die Vorteile beider Architekturmuster vereint.
Es ist eine einzige Anwendung, die jedoch in klar abgegrenzte Module unterteilt ist.

#figure(
  image("./pictures/modulith.svg"),
  caption: [
    Monolith vs. Modulith vs. Microservices
  ],
) <modulith-diagram>


=== Hexagonale Architektur

Während die Modulith-Architektur die Strukturierung auf der Ebene der gesamten Anwendung adressiert, konzentriert sich die hexagonale Architektur auf die Strukturierung innerhalb einzelner Softwarekomponenten.
BZiel ist es, die Domänenlogik klar von technischen Details und externen Systemen zu isolieren. Die Domäne befindet sich dabei im Zentrum der Architektur und sollte möglichst wenige Abhängigkeiten zu außenliegenden Systemen haben.
Technische Aspekte wie werden nicht direkt innerhalb der Domäne implementiert.

Damit die Domäne mit der Außenwelt kommunizieren kann, werden sogenannte Ports und Adapters #footnote[Deswegen wird die Hexagonale Architektur auch als Ports and Adapters Architektur bezeichnet].
Ports definieren dabei die Schnittstellen, über die die Domäne mit externen Systemen interagiert.
Dabei gibt es zwei Arten von Ports:

- *Primary Ports* #footnote[Auch bekannt als Driving, Aktive oder Inbound Ports]: Diese Ports werden von externen Systemen aufgerufen, um Aktionen innerhalb der Domäne auszulösen. Sie repräsentieren die Eingangsseite der Domäne.
- *Secondary Ports* #footnote[Auch bekannt als Driven oder Outbound Ports]: Diese Ports werden von der Domäne verwendet, um auf externe Systeme zuzugreifen. Sie repräsentieren die Ausgangsseite der Domäne.

Die Ports können dann von Adaptern implementiert werden, die die eigentliche Kommunikation mit den externen Systemen übernehmen.
Adapter sind konkrete Implementierungen der Ports und können verschiedene Technologien und Protokolle verwenden, um mit der Außenwelt zu interagieren.

Durch die Trennung von Domänenlogik und technischen Aspekten wird die Wartbarkeit und Testbarkeit der Software verbessert.
Die Domäne kann unabhängig von den äußeren Systemen entwickelt und getestet werden, was die Flexibilität und Anpassungsfähigkeit der Software erhöht.
Dieser Ansatz schützt die Domänenlogik vor Änderungen in der technischen Infrastruktur und erleichtert die Integration neuer Technologien.
Auch diese Architektur lässt sich gut mit DDD kombinieren, da sie zum einen die Domaine in den Mittelpunkt stellt und die Prinzipien der klaren Abgrenzung und der losen Kopplung unterstützt @vernon2013[p.~125-130].

In Abbildung @hexagonal-diagram ist die hexagonale Architektur dargestellt.
Im Zentrum befindet sich die Domainlogik der Anwendung.
Auf der Linken Seite wird ein Rest-Controller übere einen Adapter mit einem Primary Port der Domäne verbunden.
Auf der Rechten Seite ist eine Datenbank dargestellt.
Über einen Secondary Port kann die Domäne auf die Datenbank zugreifen.

#figure(
  image("./pictures/hexagonal.svg"),
  caption: [
    Hexagonale Architektur
  ],
) <hexagonal-diagram>

=== C4-Modell zur Architekturdokumentation

Eine verständliche und aktuelle Architekturdokumentation ist ein wesentlicher Bestandteil langlebiger Softwaresysteme.
Das C4-Modell ist ein leichtgewichtiges Modell zur Beschreibung und Visualisierung von Softwarearchitekturen.
Es verfolgt das Ziel, Architekturen auf unterschiedlichen Abstraktionsebenen klar, konsistent und zielgruppengerecht darzustellen.
Das C4-Modell adressiert ein häufiges Problem klassischer Architekturdokumentationen.
Diagramme sind entweder zu grob, um konkrete Designentscheidungen zu erklären, oder zu detailliert, um einen schnellen Überblick zu ermöglichen.

Die zentralen Vorteile des C4-Modells sind:

- *Mehrstufige Abstraktion*: Unterschiedliche Zielgruppen (Stakeholder, Architekten, Entwickler) erhalten jeweils die für sie relevante Detailtiefe.
- *Konsistenz*: Alle Diagramme bauen logisch aufeinander auf und beschreiben dasselbe System aus unterschiedlichen Perspektiven.
- *Gute Verständlichkeit*: Fokus auf Struktur und Verantwortlichkeiten statt auf technische Details.
- *Technologieunabhängigkeit*: Das Modell beschreibt was ein System ist und wie es strukturiert ist – nicht zwingend wie es implementiert wurde.

Gerade in domänengetriebenen und modularen Architekturen unterstützt das C4-Modell dabei, fachliche und technische Strukturen klar zu kommunizieren, ohne sich frühzeitig auf Implementierungsdetails festzulegen @c4model.

Das C4-Modell besteht aus vier aufeinander aufbauenden Diagrammtypen, die jeweils eine spezifische Abstraktionsebene abdecken @c4diagrams.

- *Context Diagram (System Context)*: Das Context Diagram bietet die höchste Abstraktionsebene. Es zeigt das betrachtete Softwaresystem als Ganzes und stellt dessen Beziehungen zu externen Akteuren und Systemen dar. Ziel dieses Diagramms ist es, ein gemeinsames Verständnis darüber zu schaffen, welche Rolle das System im Gesamtkontext spielt und mit wem oder was es interagiert.
- *Container Diagram*: Das Container Diagram zoomt eine Ebene tiefer in das System hinein. Es zeigt, aus welchen Containern das System besteht und wie diese miteinander kommunizieren.
- *Component Diagram*: Das Component Diagram beschreibt die innere Struktur eines einzelnen Containers. Es zeigt, aus welchen Komponenten dieser besteht und wie diese zusammenarbeiten.
- *Code Diagram (optional)*: Das Code Diagram stellt die detaillierteste Ebene dar und zeigt die konkrete Implementierung. Im C4-Modell ist dieses Diagramm optional. Der Grund dafür ist, dass der Quellcode selbst bereits eine sehr detaillierte und oft automatisch generierbare Dokumentation darstellt. Zudem ändern sich Code-Strukturen in der Regel häufiger als architektonische Konzepte.

== Kotlin

Kotlin ist eine Programmiersprache, die 2011 von JetBrains entwickelt wurde.
Ziel der Entwicklung war es, eine verbesserte Alternative zu Java zu schaffen @kotlinHandbuch[p.~19].

Bei Java handel es sich um eine weit verbreitete und etablierte Programmiersprache, die seit 1995 existiert und für ihre Plattformunabhängigkeit und Stabilität bekannt ist.

Bei der Entwicklung von Kotlin hat JetBrains bewusst aus den Designfehlern von Java gelernt.
Gleichzeitig wurden essenzielle Eigenschaften, die zur Popularität von Java beigetragen haben, beibehalten @kotlinHandbuch[p.~20].

Ich werde in dieser Arbeit zentrale Feautres von Kotlin vorstellen, die sich sehr gut mit den anderen Technologien und Patterns dieser Arbeit kombinieren lassen.
Darüber hinaus bietet Kotlin eine Vielzahl moderner Sprachfeatures, die die Entwicklung von Software erleichtern und beschleunigen.


=== Interpolarität Java

Java Code wird in Bytecode kompiliert der dann auch einer entsprechenden Laufzeitumgebung, häufig die Java Virtual Machine (JVM) genannt, ausgeführt wird.
Auch Kotlin Code wird in Bytecode kompiliert, der auf der JVM ausgeführt werden kann.
Beide Sprachen teilen sich somit die gleiche Laufzeitumgebung und können nahtlos miteinander interagieren.
Dies bietet den großen Vorteil, dass Kotlin-Programme überall dort lauffähig sind, wo auch Java-Code ausgeführt werden kann.
Zudem kann Kotlin dadurch auf sämtliche vorhandenen Java-Bibliotheken zugreifen und diese nutzen, wodurch nahezu das gesamte Java-Ökosystem zur Verfügung steht @kotlinHandbuch[p.~20].

So kann Kotlin auf der einen Seite auf den Erfolg von Java aufbauen und auf der anderen Seite eigene Features und Verbesserungen einbringen.

=== Funktionale Programmierung

Die funktionale Programmierung hat in den letzten Jahren zunehmend an Bedeutung gewonnen.
Ein wesentlicher Grund dafür liegt in der stagnierenden Entwicklung der CPU-Geschwindigkeit.
Während in früheren Jahren Leistungssteigerungen hauptsächlich durch höhere Taktfrequenzen erzielt wurden, liegt der Fokus heute verstärkt auf Mehrkernprozessoren.
Daraus ergibt sich die Möglichkeit, Programme parallel auszuführen, um die vorhandenen Ressourcen effizient zu nutzen.
Die funktionale Programmierung eignet sich besonders gut für Parallelisierungskonzepte, da sie auf Zustandslosigkeit und Nebenwirkungsfreiheit basiert.
Diese Eigenschaften vereinfachen die Ausführung von Code und machen sie zugleich sicherer @kotlinPatterns[p.~129].

Kotlin wurde von Beginn an mit dem Ziel entwickelt, eine moderne Programmiersprache bereitzustellen, die sowohl objektorientierte als auch funktionale Paradigmen unterstützt.


=== Coroutines

In modernen Anwendungen, insbesondere in solchen mit hoher Benutzerinteraktion oder paralleler Datenverarbeitung, spielt effiziente Nebenläufigkeit eine zentrale Rolle.
Wie im vorherigen Kapitel beschrieben eignet sich sich die Funktionale Programmierung, um die nebenläufige Logik deklarativ und fehlerarm zu beschreiben.
Die tatsächliche technische Ausführung von Nebenläufigkeit wird in Java durch Threads realisiert und in Kotlin durch die Coroutines.

Kotlin bietet mit Coroutines eine leichtgewichtige Lösung um Nebenläufigkeit zu implementieren.
Coroutines ermöglichen eine effizientere und zugleich einfachere Umsetzung von paralleler und asynchroner Programmierung.
Sie sind nicht an einen bestimmten System-Thread gebunden und können flexibel zwischen Threads wechseln.
Die Verwaltung der Coroutines übernimmt der Kotlin-Compiler in Verbindung mit dem Dispatcher, der auch den Overhead von Kontextwechseln reduziert.
Selbst bei einer blockierenden Coroutine bleibt der zugrunde liegende Thread frei und kann andere Coroutines verarbeiten.
So lässt sich eine deutlich bessere Ausnutzung der verfügbaren Ressourcen erzielen @kotlinPatterns[p.~195].

=== Spring Boot

Spring Boot ist ein Framework zur Entwicklung moderner Java- und Kotlin-Anwendungen und basiert auf dem Spring-Framework.
Es wurde entwickelt, um den Einstieg in Spring-basierte Projekte zu vereinfachen und typische Konfigurationsaufwände drastisch zu reduzieren.
Durch Konventionen und automatisierte Konfiguration ermöglicht Spring Boot das Erstellen produktionsreifer Anwendungen mit minimalem Setup.

Ein zentrales Ziel von Spring Boot ist es, Entwicklerinnen und Entwicklern eine schlanke, modulare und gut strukturierbare Grundlage für unterschiedlichste Softwarearchitekturen zu bieten.
Dabei fügt es sich nahtlos in gängige moderne Architekturansätze wie Domain-Driven Design, hexagonale Architektur und modulare Monolithen ein.

Spring Boot bietet eine große Auswahl an sogenannten Starter Dependencies, die es erlauben, unterschiedliche Technologien mit minimalem Konfigurationsaufwand einzubinden.

== Zusammenfassung

Die in dieser Arbeit vorgestellten Konzepte und Technologien verfolgen das übergeordnete Ziel, die Komplexität moderner Software beherrschbar zu machen.
Dies wird erreicht, indem Software in eigenständige, klar abgegrenzte Einheiten strukturiert wird, die auf verschiedenen Ebenen unabhängig und nach Möglichkeit asynchron miteinander interagieren können.
Durch die Kombination der vorgestellten Werkzeuge lassen sich sowohl die fachliche Komplexität als auch technische Herausforderungen effizient adressieren.

Auf konzeptioneller Ebene bietet DDD ein Rahmenwerk, um die fachliche Domäne präzise zu modellieren.
Subdomains, Bounded Contexts, Aggregates, Entities, Value Objects und Domain Events schaffen klare Abgrenzungen und tragen dazu bei, dass Softwarelösungen eng an der Realität der Fachprozesse ausgerichtet sind.
Event Storming unterstützt diesen Ansatz, indem es die Zusammenarbeit zwischen Entwicklern und Fachexperten erleichtert und ein gemeinsames Verständnis der Domäne sicherstellt.

Die vorgestellten Architekturen, insbesondere Modulithen und die hexagonale Architektur, ergänzen DDD ideal. Sie sorgen dafür, dass die einzelnen Domänenmodule klar strukturiert, gut wartbar und von technischen Details isoliert sind.
Ports und Adapters stellen sicher, dass die Domänenlogik unabhängig von externen Systemen entwickelt und getestet werden kann.

Event-Driven Architecture (EDA) sorgt dafür, dass diese Einheiten asynchron und strukturiert miteinander kommunizieren.
Event Sourcing erweitert diesen Ansatz, indem es Zustandsänderungen als unveränderbare Events modelliert.
Ein Event repräsentiert dabei einen Fakt, der tatsächlich eingetreten ist und nicht rückgängig gemacht werden kann.
Man kann sich ein System, das auf Events basiert, als eine Art Fortschreiten der Zeit vorstellen.
Jeder Event markiert einen Punkt auf dieser Zeitachse und trägt zur Abfolge der Ereignisse bei.
So lassen sich reale Sachverhalte nachvollziehbar abbilden, und die Software kann näher an der fachlichen Domäne entwickelt werden.
Änderungen in der Vergangenheit können nicht gelöscht werden.
Lediglich kompensierende Handlungen sind möglich, um unerwünschte Effekte zu korrigieren.
In Kombination mit CQRS entsteht eine saubere Trennung von Lese- und Schreiboperationen, die die Konsistenz der Domäne unterstützt.

Auf technischer Ebene bieten Kotlin und insbesondere seine Coroutines eine leistungsfähige Basis für nebenläufige und asynchrone Verarbeitung.
Zusammen mit Spring Boot ergeben sich konkrete Werkzeuge und Frameworks, um die theoretischen Konzepte praktisch umzusetzen.
Spring Boot unterstützt modulare Anwendungen, interne Event-Systeme und reactive Programmierung, wodurch die Umsetzung von EDA, Event Sourcing und modularem Aufbau vereinfacht wird.

Insgesamt zeigt sich, dass die vorgestellten Konzepte und Technologien auf mehreren Ebenen ineinandergreifen:

- DDD liefert die fachliche Modellierung und klare Abgrenzung von Verantwortlichkeiten.
- Modulithen und hexagonale Architektur stellen eine saubere technische Struktur bereit.
- EDA und Event Sourcing ermöglichen nachvollziehbare, asynchrone Kommunikation und Zustandsverwaltung.
- Kotlin und Spring Boot bieten die technische Grundlage für effiziente Implementierung, Nebenläufigkeit und Skalierbarkeit.

Durch diese Kombination entsteht eine Softwarearchitektur, die sowohl fachlich präzise als auch technisch robust ist, leicht erweiterbar, wartbar und skalierbar und damit den Herausforderungen moderner, komplexer Anwendungen gerecht wird.

= Implementierung

In diesem Kapitel wird die Umsetzung der in den vorherigen Kapiteln vorgestellten Konzepte und Technologien anhand eines Beispielprojekts erläutert.
Ziel ist es, die praktische Anwendung der theoretischen Grundlagen darzustellen und zu zeigen, wie diese miteinander kombiniert werden können, um eine modulare, wartbare und skalierbare Softwarelösung zu realisieren.

Als Domain für das Beispielprojekt wurde eine fiktive Parkplatzverwaltungsanwendung gewählt.
Diese Domain zeichnet sich durch einen klar abgegrenzten fachlichen Kontext aus, der in der physischen Welt verortet ist.
Die Anzahl der Nutzer ist dabei an die reale Kapazität des Parkplatzes gebunden, wodurch plötzliche Lastspitzen als unwahrscheinlich angesehen werden können.

Darüber hinaus weist die Parkplatzdomain eine Reihe von Zustandsänderungen auf, die sich gut durch Events abbilden lassen.
Innerhalb des Systems entsteht dadurch ein dynamisches Verhalten, da Events fortlaufend den Zustand der Domain verändern und weitere fachliche Reaktionen auslösen können.
Dazu zählen unter anderem das Ein- und Ausfahren von Fahrzeugen sowie das Reservieren und Freigeben von Parkplätzen.
Die Domain ist vergleichsweise einfach verständlich und erfordert kein spezielles Fachwissen.
Gleichzeitig lassen sich typische Anwendungsfälle klar definieren, die notwendig sind, um zentrale Aspekte von DDD und EDA zu demonstrieren.

Der vollständige Quellcode des Beispielprojekts ist in meinem GitHub-Repository verfügbar.
In der Datei `README.md` befindet sich eine kurze Anleitung zur Nutzung des Repositories#footnote[GitHub-Repository: #link("https://github.com/FSpruhs/park-flow")].

== Vorstellung von Parkflow

Das Beispielprojekt trägt den Namen *Parkflow*.
Ziel der Anwendung ist die Verwaltung eines Parkplatzes sowie die Steuerung des laufenden Parkbetriebs.
Hierbei wird angenommen, dass sämtliche Ein- und Ausgänge sowie alle Parkplätze mit Sensoren ausgestattet sind, die Aktionen von Fahrzeugen erkennen und entsprechende Ereignisse auslösen#footnote[Das Verhalten der Sensoren wird in dieser Arbeit simuliert.].
Auf dieser Grundlage können verschiedene Abläufe innerhalb des Systems automatisiert werden.

In einem ersten Schritt soll der Parkplatzbetreiber in der Lage sein, Parkplätze sowie Ein- und Ausgänge im System anzulegen und zu verwalten.
Dazu zählen unter anderem das Anlegen und Entfernen von Inventar, das temporäre Aktivieren oder Deaktivieren einzelner Elemente, die Festlegung von Preisen sowie die Änderung von Parkplatztypen#footnote[Beispiele: Entfernen von Inventar, temporäres Deaktivieren oder Aktivieren, Preisgestaltung, Änderung von Parkplatztypen.].
Innerhalb des Beispielprojekts werden mehrere Parkplatztypen berücksichtigt, darunter reguläre Parkplätze, Behindertenparkplätze, Parkplätze für Elektrofahrzeuge sowie monatlich mietbare Parkplätze.

Auf Seiten der Parkplatznutzer besteht die Möglichkeit, einen Benutzeraccount anzulegen und Zahlungsinformationen zu hinterlegen.
Darüber hinaus können Fahrzeuge über ihr Kennzeichen registriert werden.
Zusätzlich ist vorgesehen, dass Parkplätze für einen Monat angemietet werden können.

Neben der Verwaltung von Parkplätzen und Nutzern umfasst das System auch den laufenden Parkbetrieb.
Fährt ein Fahrzeug an einen Eingang heran, wird das Kennzeichen über einen Sensor erfasst und geprüft, ob das Fahrzeug im System registriert ist.
Ist dies der Fall, wird dem Fahrzeug ein geeigneter Parkplatz zugewiesen und angezeigt.
Anschließend wird das Einfahrtstor geöffnet und das Fahrzeug kann einfahren.
Über einen Sensor am Parkplatz wird dem System mitgeteilt, welches Fahrzeug dort abgestellt wurde.
Verlässt das Fahrzeug den Parkplatz, wird das Kennzeichen am Ausgang erneut erfasst, das Ausfahrtstor geöffnet und der Ausfahrvorgang ermöglicht.
Gleichzeitig wird auf Basis der Parkdauer eine Rechnung erstellt und das hinterlegte Zahlungsmittel belastet.

Die Anwendung ist darauf ausgelegt, den Parkplatzbetrieb für Anlagen unterschiedlicher Größenordnungen zu unterstützen.
Dabei dürfen ausschließlich registrierte Fahrzeuge den Parkplatz betreten.
Durch die Zuordnung von Fahrzeugen zu Parkplätzen soll eine möglichst effiziente Auslastung der verfügbaren Stellflächen erreicht werden.
Parkplätze mit besonderem Zweck, wie beispielsweise Behinderten- oder Elektrofahrzeugparkplätze, werden bevorzugt an entsprechend geeignete Fahrzeuge vergeben.
Das Verfahren zur Parkplatzzuweisung ist dabei flexibel gestaltet, sodass der Parkplatzbetreiber zwischen verschiedenen Strategien wählen und diese auch während des laufenden Betriebs wechseln kann.
Ein mögliches Szenario ist beispielsweise ein Parkplatz eines Fußballstadions, bei dem an Spieltagen ausschließlich Fahrzeuge von Ticketinhabern zugelassen werden, während an spielfreien Tagen auch andere Nutzer berücksichtigt werden.

Da es sich bei Parkflow um ein Beispielprojekt handelt, konzentriert sich die Implementierung auf die Kernfunktionen, die erforderlich sind, um die in dieser Arbeit vorgestellten Konzepte zu demonstrieren.
Die folgenden Aspekte werden daher nicht berücksichtigt:

- Sicherheitsmechanismen wie Authentifizierung und Autorisierung#footnote[Standardisierte Verfahren wie OAuth 2.0 könnten grundsätzlich ergänzt werden, sind jedoch nicht Teil dieser Arbeit.].
- In den meisten Anwendungsfällen wird primär der reguläre Ablauf (Happy Path) implementiert. Ergänzend werden lediglich ausgewählte Fehlerfälle betrachtet. Eine vollständige Behandlung aller Rand- und Sonderfälle würde den Rahmen dieser Arbeit überschreiten.
- Es wird keine grafische Benutzeroberfläche implementiert.
- Rechtliche und regulatorische Anforderungen bleiben unberücksichtigt.
- Es werden ausschließlich registrierte Fahrzeuge betrachtet.
- Es werden ausschließlich Fahrzeuge mit deutschen Kennzeichen berücksichtigt.
- Die räumliche Lage einzelner Parkplätze wird nicht modelliert.

== Erkunden der Domain

Die Domain des Beispielprojekts wird im Rahmen eines Event Storming Workshops exploriert.
Event Storming wird in der Regel als kollaborativer Prozess mit mehreren Teilnehmern durchgeführt, um unterschiedliche Perspektiven in die Modellierung einzubeziehen.
Aufgrund fehlender externer Fachexperten wird die Rolle des Domänenexperten in dieser Arbeit jedoch von mir selbst übernommen, wodurch der Workshop allein durchgeführt wird.
In dieser Rolle werden die Use Cases modelliert, die im Rahmen dieser Arbeit implementiert werden sollen.

Da keine Diskussionen mit weiteren Teilnehmern stattfinden, lässt sich der Workshop nur eingeschränkt abbilden.
Ziel ist nicht die Erstellung eines vollständigen oder perfekten Modells, sondern die Demonstration des Event Storming-Prozesses und die Verdeutlichung der Bedeutung von Events innerhalb der Domain.

Auch wenn die fehlende Perspektivenvielfalt den Prozess einschränkt, ist es dennoch möglich, die Domäne zu modellieren und die zentralen Konzepte zu identifizieren.
Um die Übersichtlichkeit zu wahren, werden nicht alle Schritte grafisch dargestellt.
Sämtliche Abbildungen zum Workshop sind in der Dokumentation von Parkflow verfügbar#footnote("/doc/eventstorming").

=== Schritt 1: Unstrukturiertes Erforschen

Im ersten Schritt notieren die Teilnehmer ausschließlich die Namen von Ereignissen, die ihnen zur Domain einfallen, auf orangefarbenen Klebezetteln und platzieren diese zunächst unstrukturiert an einer Wand#footnote[Workshops können auch mit entsprechenden digitalen Tools durchgeführt werden.].

Bei den Ereignissen handelt es sich um fachliche Ereignisse in der Vergangenheitsform @khononov2022[p.~217].

#figure(
  image("./doc/eventstorming/01-unstrukturiertes-erforschen.svg"),
  caption: [
    Ergebnis Unstrukturiertes Erforschen
  ],
) <unstrukturiertes-erforschen>

=== Schritt 2: Zeitache

Im zweiten Schritt werden die Events auf einer horizontalen Zeitachse angeordnet.
Dabei beginnt die Darstellung mit dem frühesten Event links und endet mit dem aktuellsten Event rechts.
Bei der Anordnung wird vom Happy Path ausgegangen.
Events, die gleichzeitig auftreten, werden vertikal untereinander platziert @khononov2022[p.~218].

#figure(
  image("./doc/eventstorming/02-zeitachse.svg"),
  caption: [
    Ergebnis Zeitachse
  ],
) <zeitachse>

In Abbildung @zeitachse ist zu erkennen, dass die zuvor unstrukturierten Events in fünf verschiedene Zeitachsen unterteilt wurden.
Drei Zeitachsen betreffen die Verwaltung von Parkplätzen, Toren und Kunden, die in ihrer Struktur sehr ähnlich aufgebaut sind.
Zunächst wird das jeweilige Objekt angelegt.
Anschließend können verschiedene Eigenschaften geändert, hinzugefügt oder entfernt werden.

Die vierte Zeitachse zeigt den Lifecycle eines Fahrzeugs während eines Parkplatzbesuchs.
An einem Punkt teilt sich die Zeitachse in zwei Pfade und führt später wieder zusammen.
Dies geschieht, wenn ein Fahrzeug auf einem Parkplatz parkt.
Es kann entweder auf dem zugewiesenen Parkplatz oder auf einem anderen Parkplatz abgestellt werden.
Im Falle des Parkens auf einem nicht zugewiesenen Parkplatz werden zusätzliche Events ausgelöst.

Die fünfte Zeitachse stellt den Bezahlvorgang dar.

=== Schritt 3: Pain Points

Im dritten Schritt werden Pain Points identifiziert und markiert.
Dabei handelt es sich um Stellen im Prozess, die problematisch, ineffizient oder fehleranfällig sein können.
Pain Points werden mit rautenförmigen, pinkfarbenen Klebezetteln dargestellt @khononov2022[p.~219].

Für Parkflow wird ein Pain Point bei den Events ParkedOn und ParkedOnWrong identifiziert.
Dies verdeutlicht die potenzielle Gefahr einer unkontrollierten Dynamik, die entstehen kann, wenn Fahrzeuge einander die zugewiesenen Parkplätze „wegnehmen“ und die neu zugewiesenen Parkplätze nicht korrekt beim jeweiligen Fahrzeug ankommen.

=== Schritt 4: Pivotal Events

Im vierten Schritt werden Pivotal Events identifiziert und markiert.
Dabei handelt es sich um Events, die einen Übergang des Prozesses in eine andere Phase auslösen.
Pivotal Events werden mit einem vertikalen Strich dargestellt @khononov2022[p.~219-220].

=== Schritt 5: Commands

Im fünften Schritt werden Commands identifiziert.
Bei Commands handelt es sich um Anweisungen, die eine Aktion innerhalb des Systems auslösen.
Ein Command führt zu einem Event, sobald die entsprechende Aktion erfolgreich abgeschlossen wurde.
Aus diesem Grund werden Commands auf hellblauen Klebezetteln vor dem zugehörigen Event platziert.

Zusätzlich kann ein Actor, der einen Command ausführt, auf einem gelben Klebezettel vermerkt und an den jeweiligen Command angeheftet werden.
Wird eine Folge von Commands von demselben Actor ausgeführt, kann der Actor auch über die gesamte Abfolge hinweg dargestellt werden @khononov2022[p.~220-221].

Für Parkflow werden zwei Actor unterschieden.
Der Parkplatzbetreiber und der Kunde.
Der Parkplatzbetreiber ist für die Verwaltung des Parkplatzinventars zuständig und führt die entsprechenden Commands aus.
Der Kunde ist für die Registrierung seiner Fahrzeuge sowie das Mieten von Parkplätzen verantwortlich und führt die entsprechenden Commands aus.

#figure(
  image("./doc/eventstorming/05-commands.svg"),
  caption: [
    Ergebnis Commands
  ],
) <commands>

=== Schritt 6: Policies

Im sechsten Schritt werden Policies identifiziert.
Policies sind Regeln oder Bedingungen, die festlegen, wie das System auf bestimmte Events reagieren soll.
Dabei handelt es sich in der Regel um Commands, die nicht von einem Actor ausgelöst werden, sondern automatisch als Reaktion auf ein Event ausgeführt werden.
Policies werden auf lilafarbenen Klebezetteln dargestellt und zwischen dem auslösenden Event und dem resultierenden Command platziert @khononov2022[p.~221].

Für Parkflow existieren beispielsweise folgende Policies.
Wird ein Fahrzeug an einem Eingang erkannt, löst das System automatisch den Command `ProvideParkingSpot` aus, um dem Fahrzeug einen Parkplatz zuzuweisen. Ebenso wird durch eine Policy automatisch eine Rechnung erstellt, sobald das Fahrzeug den Parkplatz verlässt.

=== Schritt 7: Read Models

Im siebten Schritt werden Read Models identifiziert.
Read Models werden von einem Actor verwendet, um Entscheidungen über das Ausführen von Commands zu treffen.
Dabei handelt es sich nicht um technische Darstellungen von Datenbanken, sondern um fachliche Konzepte, die dem Actor helfen, den aktuellen Zustand der Domäne zu verstehen.
Viele Read Models lassen sich direkt aus realen Konzepten ableiten, wie beispielsweise der aktuelle Parkplatzbestand oder die Historie der Fahrzeuge.
Dies erleichtert die Abbildung des Systems in einer Weise, die für die Domainakteure verständlich und nachvollziehbar ist.
Read Models werden auf grünen Klebezetteln dargestellt.
Da der Actor die Informationen benötigt, bevor er die Commands ausführt, werden die Read Models vor den Commands platziert @khononov2022[p.~222].

Bei Parkflow ergeben sich folgende Read Models:

- *ParkingInventory*: Gibt einen Überblick über alle Parkplätze sowie Ein- und Ausgänge, deren Status und Typen. Dieses Read Model unterstützt den Parkplatzbetreiber dabei, den aktuellen Zustand des Parkplatzes zu verstehen und Entscheidungen über die Verwaltung des Inventars zu treffen.
- *ParkingSpotCatalog*: Bietet dem Kunden einen Überblick über die mietbaren Parkplätze und deren Preise.
- *ParkingMap*: Stellt den laufenden Betrieb des Parkplatzes dar. Es zeigt an, welche Parkplätze belegt oder frei sind und welche Fahrzeuge sich zu diesem Zeitpunkt im System befinden. Dieses Read Model unterstützt das System bei der Zuweisung eines geeigneten Parkplatzes an ein Fahrzeug.
- *FeeCatalog*: Bietet eine Übersicht über die verschiedenen Parkgebühren und deren Berechnungsgrundlagen. Dieses Read Model ermöglicht dem System, die korrekten Gebühren für die Parkdauer zu berechnen.
- *VehicleHistory*: Dokumentiert die Aktionen eines Fahrzeugs im System. Dieses Read Model unterstützt die Erstellung einer Rechnung, sobald das Fahrzeug den Parkplatz verlässt.

=== Schritt 8: Externe Systeme

Im achten Schritt werden externe Systeme identifiziert.
Externe Systeme sind Systeme außerhalb der eigenen Domäne, mit denen das System interagiert.
Dies können beispielsweise Zahlungssysteme, Benachrichtigungssysteme oder andere Drittanbietersysteme sein.
Externe Systeme werden auf pinkfarbenen Klebezetteln dargestellt und an den Stellen im Prozess platziert, an denen die Interaktion mit dem externen System erfolgt @khononov2022[p.~223].

Für Parkflow wird ein externes Zahlungssystem genutzt, um die Parkgebühren automatisch vom hinterlegten Zahlungsmittel des Kunden abzubuchen, sobald das Fahrzeug den Parkplatz verlässt.

=== Schritt 9: Aggregates

Im neunten Schritt werden Aggregates identifiziert.
Aggregates werden auf großen gelben Klebezetteln dargestellt.
Um jeden Aggregate-Klebezettel werden auf der linken Seite die zugehörigen Commands und auf der rechten Seite die zugehörigen Events platziert @khononov2022[p.~223].
Auf diese Weise wird ersichtlich, welche Commands und Events zu welchem Aggregate gehören.

Für Parkflow ergeben sich nach diesem Schritt die Aggregates `ParkingSpot`, `Gate`, `Customer`, `ParkingOperator` und `Invoice`.

=== Schritt 10: Bounded Contexts

Im zehnten und letzten Schritt werden Bounded Contexts identifiziert.
Bounded Contexts sind klar abgegrenzte Bereiche innerhalb der Domain, die jeweils eine eigene Sprache und ein eigenes Modell besitzen @khononov2022[p.~224].
Sie werden durch einen Rahmen dargestellt, der die zugehörigen Aggregates, Commands, Events, Read Models und externen Systeme umfasst.

Für Parkflow lassen sich, wie in Abbildung @bounded-contexts zu sehen, folgende Bounded Contexts unterscheiden:

- *ParkingInventory*: Verwaltung des Parkplatzinventars, einschließlich der Parkplätze sowie Ein- und Ausgänge.
- *CustomerAcces*: Ermöglicht dem Kunden, sich zu registrieren und Fahrzeuge zu hinterlegen.
- *ParkingOperation*: Steuert den laufenden Betrieb des Parkplatzes, einschließlich der Zuweisung von Parkplätzen und der Überwachung des Parkvorgangs.
- *Billing*: Verantwortlich für die Abrechnung und Zahlung der Parkgebühren.

#figure(
  image("./doc/eventstorming/10-bounded-contexts.svg"),
  caption: [
    Ergebnis Bounded Contexts
  ],
) <bounded-contexts>

=== Subdomains <subdomains-chapter>

In diesem Schritt werden die verschiedenen Subdomains identifiziert und den drei in DDD bekannten Typen zugeordnet.
Dazu wird die Definition aus @khononov2022[p.~35], verwendet.
Die Einteilung erfolgt nach dem Verhältnis zwischen der Komplexität der Business-Logik und dem möglichen Wettbewerbsvorteil gegenüber Konkurrenten.

In Abbildung @subdomains ist die Einteilung der Subdomains von Parkflow dargestellt.

- Die Subdomain ParkingOperation stellt eine Core Domain dar. Die Verwaltung des operativen Betriebs ist der zentrale Wettbewerbsfaktor von Parkflow und beinhaltet die höchste Komplexität. Diese Domain macht die Anwendung besonders und soll sie von Konkurrenzprodukten abheben. Die Komplexität ergibt sich daraus, dass viele Events zeitnah verarbeitet werden müssen und entstehende Konflikte schnell gelöst werden müssen.
- Die Subdomains CustomerAccess und ParkingInventory sind Supporting Domains. Sie sind notwendig, um den Betrieb von Parkflow zu ermöglichen, stellen jedoch keinen direkten Wettbewerbsvorteil dar. Die Verwaltung von Kunden und Parkplätzen ist vergleichsweise einfach.
- Die Subdomain Billing wird als Supporting/Generic Domain eingeordnet. Sie ist für die Abrechnung der Parkgebühren verantwortlich, stellt aber keinen zentralen Wettbewerbsvorteil dar.
- Der Zahlungsprozess selbst wird als Generic Domain eingeordnet. Die Abwicklung von Zahlungen ist rechtlich und sicherheitstechnisch komplex, stellt jedoch eine Standardaufgabe dar, die von vielen Drittanbietern übernommen werden kann. Für Parkflow wird daher vorgesehen, dass dieser Prozess über ein externes Zahlungssystem abgewickelt wird.

#figure(
  image("./pictures/subdomains.svg"),
  caption: [
    Subdomains
  ],
) <subdomains>

=== Ubiquitous Language

Für die Ubiquitous Language habe ich in der Dokumentation von Parkflow einen Glossar #footnote[/doc/glossary] erstellt.
Dieser Glossar enthält die wichtigsten Begriffe und Konzepte, die in den verschiedenen Bounded Contexts von Parkflow verwendet werden.

== Architektur

Im vorherigen Kapitel wurde die Domain von Parkflow erkundet und modelliert.
Auf dieser Grundlage wird in diesem Kapitel die Architektur von Parkflow entworfen und dokumentiert.

Die Architekturdokumentation wurde nach dem C4-Modell erstellt#footnote[https://github.com/FSpruhs/park-flow/tree/master/doc/architecture/c4].
Dieses Modell ermöglicht eine schrittweise Beschreibung der Architektur auf unterschiedlichen Abstraktionsebenen und unterstützt damit ein strukturiertes Verständnis des Gesamtsystems.

Im Rahmen der Dokumentation werden auch Architekturteile dargestellt, die nicht im Zuge dieser Arbeit implementiert wurden.
Ziel ist es, ein ganzheitliches Bild zu vermitteln, wie eine vollständige Architektur für Parkflow aussehen könnte, und die implementierten Teile in diesen größeren architektonischen Kontext einzuordnen.

Darüber hinaus werden in diesem Kapitel Werkzeuge vorgestellt, die dabei unterstützen, die entworfene Architektur in der Implementierung einzuhalten und abzusichern.
Dazu zählen insbesondere Spring Modulith und ArchUnit, mit deren Hilfe architektonische Strukturen, Abhängigkeiten und Modulgrenzen explizit definiert und überprüft werden können.

=== System Context

Im Zentrum von Abbildung @system-context ist das System Parkflow dargestellt.
Es gibt zwei Akteure die mit dem System interagieren.
Der Parkplatzbetreiber ist für die Verwaltung des Parkplatzinventars zuständig.
Der Kunde ist für die Registrierung seiner Fahrzeuge und das Mieten von Parkplätzen zuständig.
Es soll potentzielle Schnittstellen für externe Systeme geben die ebenfalls mit Parkflow interagieren können.
Das könnte z.B. ein Ticketsystem sein bei einem Parkplatz an einem Fussballstadion.

Weiterhin gibt es ein externes Zahlungssystem, das für die Abwicklung der Zahlungen zuständig ist und ein externes Authentifizierungssystem, das für die Authentifizierung und Autorisierung der Nutzer verantwortlich ist.
Darüber hinaus verfügt der Parkplatz über verschiedene Sensoren, die die Aktionen der Fahrzeuge erkennen und entsprechende Events auslösen.

Im Zentrum der Abbildung @system-context ist das System Parkflow dargestellt.
Das System stellt die zentrale Anwendung zur Verwaltung und zum Betrieb eines Parkplatzes dar und bildet die Schnittstelle zwischen den beteiligten Akteuren, technischen Systemen und physischen Komponenten.

Es gibt zwei primäre Akteure, die direkt mit Parkflow interagieren.

Der Parkplatzbetreiber ist für die Verwaltung des Parkplatzinventars verantwortlich.
Dazu gehören unter anderem die Anlage, Konfiguration und Verwaltung von Parkplätzen sowie die Verwaltung von Ein- und Ausgängen.

Der Kunde nutzt das System, um sich zu registrieren, Fahrzeuge zu hinterlegen und Parkplätze zu mieten.
Während des laufenden Parkplatzbetriebs interagiert der Kunde nicht direkt mit dem System, sondern wird über automatisierte Prozesse geführt, beispielsweise bei der Ein- und Ausfahrt oder bei der Abrechnung der Parkgebühren.

Neben den direkten Akteuren sind potenzielle externe Systeme vorgesehen, die mit Parkflow interagieren können.
Diese Schnittstellen ermöglichen es, Parkflow in bestehende Systemlandschaften zu integrieren.
Ein mögliches Beispiel hierfür ist ein Ticketsystem im Kontext eines Parkplatzes an einem Fußballstadion, über das zusätzliche Zugangs- oder Berechtigungsinformationen bereitgestellt werden könnten.

Für die Abwicklung finanzieller Transaktionen ist ein externes Zahlungssystem angebunden.
Dieses System übernimmt die Verarbeitung von Zahlungen und entlastet Parkflow von sicherheits- und regulatorisch relevanten Aspekten der Zahlungsabwicklung.

Zusätzlich ist ein externes Authentifizierungssystem vorgesehen, das für die Authentifizierung und Autorisierung der Nutzer verantwortlich ist.
Dadurch wird eine klare Trennung zwischen fachlicher Logik und sicherheitsrelevanten Funktionen erreicht.

Darüber hinaus interagiert Parkflow mit verschiedenen physischen Komponenten in Form von Sensoren.
Diese Sensoren erfassen Aktionen der Fahrzeuge, wie das Einfahren, Parken oder Ausfahren, und lösen entsprechende Events im System aus.
Sie stellen damit eine wichtige Verbindung zwischen der physischen Welt und der fachlichen Logik von Parkflow dar.

#figure(
  image("./doc/architecture/c4/level-1-system-context/level-1-0.svg"),
  caption: [
    System Context
  ],
) <system-context>

=== Container <container-chapter>

In Abbildung @container ist die Container-Architektur von Parkflow dargestellt.
Die Abbildung zeigt die zentralen technischen Bausteine des Systems sowie deren Beziehungen zueinander.
Die rot markierten Container werden im Rahmen dieser Arbeit nicht implementiert und dienen ausschließlich der Darstellung einer möglichen vollständigen Systemarchitektur.

Parkflow besteht im Kern aus einem Backend, das als Modulith umgesetzt ist und die zentrale Geschäftslogik sowie das Domänenmodell enthält.
Dieses Backend stellt die fachlichen Funktionen bereit und koordiniert die Verarbeitung der eingehenden Events.

Zusätzlich sind in der Architektur zwei Frontend-Container vorgesehen, die jeweils von einem der beiden Akteure genutzt werden können.
Ein Frontend richtet sich an den Parkplatzbetreiber und ermöglicht die Verwaltung des Parkplatzinventars sowie betrieblicher Einstellungen.
Das zweite Frontend ist für Kunden vorgesehen und dient unter anderem der Registrierung von Fahrzeugen und dem Mieten von Parkplätzen.
Diese Frontends werden in dieser Arbeit nicht implementiert, da der Fokus auf der Backend-Architektur liegt.

Das Backend greift auf zwei unterschiedliche Datenbanken sowie auf eine Message Queue zu:

*MongoDB:*

MongoDB wird zur Speicherung der aktuellen Zustände von Read Models sowie von Aggregates verwendet, für die kein Event-Sourcing-Mechanismus implementiert wird.

MongoDB eignet sich besonders gut zur Speicherung von Aggregates, da ein Aggregate im Sinne von DDD eine klare transaktionale Konsistenzgrenze bildet.
Diese Grenze lässt sich in MongoDB sehr natürlich abbilden, indem ein gesamtes Aggregate als einzelnes Dokument unter einem eindeutigen Schlüssel gespeichert wird.
Änderungen an einem Aggregate können dadurch atomar durchgeführt werden, ohne dass verteilte Transaktionen oder komplexe Joins erforderlich sind.

Auch für Read Models bietet MongoDB deutliche Vorteile.
Read Models dürfen gezielt auf Lesezugriffe optimiert sein und müssen nicht der Struktur des Write Models entsprechen.
Die dokumentenorientierte Struktur von MongoDB erlaubt es, unterschiedliche fachliche Sichten auf dieselben Daten abzubilden.
Dabei wird bewusst in Kauf genommen, dass Daten mehrfach gespeichert oder denormalisiert werden, um Abfragen einfach, performant und fachlich verständlich zu halten.

Der zentrale Vorteil dieses Ansatzes liegt in der Einfachheit der Persistenz.
Der Fokus verschiebt sich weg von einer stark normalisierten Datenhaltung hin zu einer klar strukturierten, fachlich motivierten Persistenz.

Im Kontext eines Modulithen gilt zudem ein zentrales Architekturprinzip.
Jedes Modul darf ausschließlich auf seine eigenen persistenten Daten zugreifen.
Daten werden niemals über Modulgrenzen hinweg gespeichert oder direkt gelesen.
Diese Trennung schützt die fachlichen Grenzen, verhindert enge Kopplung zwischen Modulen und unterstützt eine spätere Weiterentwicklung der Architektur.

*Postgres:*

PostgreSQL wird als Event Store eingesetzt, um die Historie aller Ereignisse im System zu speichern.
Für Event Sourcing ist es entscheidend, dass jedes Event unveränderlich ist und dass die chronologische Reihenfolge der Ereignisse erhalten bleibt.

Die Speicherung erfolgt in einer einfachen Tabelle, in der jede Zeile genau einem Event entspricht.
Neue Events werden fortlaufend angehängt, sodass die Tabelle im Prinzip eine lineare Liste von Events darstellt.
Diese Struktur entspricht dem grundlegenden Prinzip von Event Sourcing.
Die gesamte Historie der Änderungen wird vollständig und unverändert gespeichert.

Durch die tabellarische Struktur von PostgreSQL lassen sich Events einfach sequenziell ablegen und in der richtigen Reihenfolge wieder auslesen.
Jede Zeile enthält dabei alle relevanten Informationen des Events, wie Typ, Zeitstempel, zugehöriges Aggregate und die Event-Daten selbst.

*RabbitMQ:*

RabbitMQ wird als Event Queue verwendet, um die von den Sensoren erzeugten Events zuverlässig an das Backend weiterzuleiten.
Die Sensoren veröffentlichen ihre Events in der Queue, während das Backend diese asynchron konsumiert und verarbeitet.

Durch diese Architektur entsteht eine lose Kopplung zwischen Sensoren und Backend.
Das Backend ist nicht direkt an die Verfügbarkeit oder Antwortzeiten der Sensoren gebunden, wodurch Lastspitzen abgefedert und eine skalierbare Verarbeitung der Events ermöglicht wird.

#figure(
  image("./doc/architecture/c4/level-2-container/level-2-0.svg"),
  caption: [
    Container
  ],
) <container>

=== Component

In Abbildung @component ist die Komponentenstruktur des Backend von Parkflow dargestellt.
Die Abbildung zeigt die einzelnen Module, die den verschiedenen Bounded Contexts entsprechen, sowie deren Interaktionen untereinander.

Für jeden Bounded Context existiert ein eigenes Modul.
Diese Module kapseln die fachliche Logik des jeweiligen Kontexts und bilden die Grundlage für eine modulare und wartbare Architektur.
Die Pfeile zwischen den Modulen stellen den Fluss von Events dar, der über ein internes Event-System realisiert wird.
Auf diese Weise können Module unabhängig voneinander arbeiten und dennoch auf relevante Ereignisse anderer Module reagieren, ohne direkt gekoppelt zu sein.

Die Module sind so gestaltet, dass sie möglichst autark operieren können.
Dadurch wird eine lose Kopplung zwischen den Bounded Contexts erreicht, was die Wartbarkeit, Erweiterbarkeit und potenzielle Skalierbarkeit des Systems unterstützt.
Jedes Modul verwaltet zudem seine eigenen persistenten Daten, sodass Änderungen an einem Modul keine direkten Auswirkungen auf andere Module haben.

Zusätzlich existiert ein gemeinsames common-Modul, das gemeinsame Funktionalitäten und Utilities bereitstellt, die von mehreren Modulen genutzt werden können.
Dazu gehören beispielsweise allgemeine Datenstrukturen, Utility-Klassen, gemeinsame Schnittstellen oder Basisklassen für Events.
Das common-Modul dient somit als Wiederverwendungsschicht, ohne die fachliche Unabhängigkeit der einzelnen Module zu verletzen.

Durch die modulare Struktur und die Event-basierte Kommunikation bleibt das System intern konsistent.
Jedes Modul verarbeitet seine eigenen Änderungen und teilt die resultierenden Events dem Rest des Systems mit.
Andere Module können diese Events asynchron konsumieren, wodurch die Kommunikation entkoppelt erfolgt und Module unabhängig voneinander arbeiten können.

#figure(
  image("./doc/architecture/c4/level-3-components/level-3-0.svg"),
  caption: [
    Component
  ],
) <component>

=== Code

In Abbildung @code ist die interne Struktur des Moduls `ParkingInventory` dargestellt.
Jedes Modul besteht auf der obersten Ebene aus den zwei Paketen `api` und `core`.
Das `api` Paket enthält den Code der von anderen Modulen genutzt werden darf.
Dazu gehören die Events die ein Modul veröffentlich, die gemeinsam genutzten Value objects und Schnittstellen für die Interaktion mit dem Modul.

Das `core` Paket enthält die interne Implementierung des Moduls und darf von anderen Modulen nicht genutzt werden.
Dabei ist das `core` Paket als hexagonale Architektur umgesetzt.
Das `core` Paket besteht aus drei Hauptpaketen:

- *infrastructure*: Die Infrastruktur enthält die technische Umsatzung die als Adapter implementiert werden. Über die Adapter werden die Datenbanken angebunden, die Rest und RabbitMQ Schnittstellen implementiert und weitere technische Details umgesetzt.
- *application*: Die Application Schicht enthält die Ports die von den Adapter implementiert werden. Die Hauptaufgabe dieser Schicht ist es die Ressourcen die für einen Usecase benötigt werden zu koordinieren. Dabei soll möglichst wenig fachliche Logik implementiert werden.
- *domain*: Die Domain Schicht enthält die fachliche Logik des Moduls. Hier werden die Aggregates, Entities, Value Objects und Domain Services implementiert. Diese Schicht ist der Kern der Anwendung. Alle Abhängigkeiten zeigen auf diese Schicht während die Schicht selber so wenig wie möglich Abhängkeiten hat.

Darüber hinaus gibt es noch das `common` Paket, das gemeinsame Funktionalitäten enthält, die von mehreren Modulen genutzt werden können.
In Parkflow enthält das Modul die die Implementierung für das Event-Sourcing System und das interne Event-System.

In Abbildung @code ist die interne Struktur des Moduls `ParkingInventory` dargestellt.
Jedes Modul ist auf oberster Ebene in die Pakete api und core gegliedert.

Das api-Paket enthält alle Elemente, die von anderen Modulen genutzt werden dürfen. Dazu gehören insbesondere:

- die von diesem Modul veröffentlichten Events,
- gemeinsam genutzte Value Objects,
- Schnittstellen für die Interaktion mit dem Modul.

Das core-Paket enthält die interne Implementierung des Moduls und darf von anderen Modulen nicht direkt verwendet werden.
Dieses Paket ist in Form einer hexagonalen Architektur umgesetzt, um eine klare Trennung zwischen fachlicher Logik und technischer Infrastruktur zu gewährleisten.

Die Hauptpakete im core-Paket sind:

- *infrastructure*: Die Infrastruktur implementiert die technischen Adapter. Hier werden Datenbanken angebunden, REST- und RabbitMQ-Schnittstellen realisiert und weitere technische Details umgesetzt.
- *application*: Die Application-Schicht koordiniert die Verarbeitung von Anfragen, die aus den Adaptern an das Modul gestellt werden. Sie dient als Orchestrator, der die entsprechenden Domain-Objekte aufruft, die fachliche Logik ausführt und die notwendigen Schritte eines Use Cases zusammenführt. Dabei implementiert sie die Ports, die von den Adaptern genutzt werden, ohne selbst komplexe fachliche Logik zu enthalten. Die Application-Schicht stellt somit sicher, dass die Domain korrekt genutzt wird und die fachlichen Abläufe konsistent ablaufen, während die technischen Details in den Adaptern verbleiben.
- *domain*: Die Domain-Schicht enthält die fachliche Logik des Moduls. Hier werden Aggregates, Entities, Value Objects und Domain Services implementiert. Diese Schicht bildet den Kern der Anwendung. Abhängigkeiten zeigen nach außen auf diese Schicht, während sie selbst so wenige Abhängigkeiten wie möglich besitzt, um maximale Kohäsion zu erreichen.

Zusätzlich existiert das common-Paket, das gemeinsame Funktionalitäten bereitstellt, die von mehreren Modulen genutzt werden.
In Parkflow enthält dieses Paket unter anderem die Implementierung des Event-Sourcing-Systems sowie des internen Event-Systems, über das Module asynchron miteinander kommunizieren können.

Auf diese Weise unterstützt die modulare Paketstruktur eine klare Trennung von Schnittstellen, Fachlogik und Infrastruktur, erleichtert die Wartbarkeit und ermöglicht eine konsistente Implementierung der Event-basierten Architektur.


#figure(
  image("./doc/architecture/c4/level-4-code/level-4-0.svg"),
  caption: [
    Component
  ],
) <code>

=== Spring Modulith

Spring Modulith #footnote[org.springframework.modulith:spring-modulith-starter-test] ist ein ein ein offizielles Spring-Projekt, das speziell für die Entwicklung modularer Monolithen entwickelt wurde.
Es bietet eine Reihe von Werkzeugen und Best Practices, um die Strukturierung, Kommunikation und Verwaltung von Modulen innerhalb eines Monolithen zu sicher zu stellen.
Spring Modulith etabliert Regeln und Konventionen, die sicherstellen, dass Module klar abgegrenzt sind und nur über definierte Schnittstellen miteinander kommunizieren.
Als Standard dürfen Module nur auf Code zugreifen, der in der Paketstruktur unterhalb des eigenen Moduls liegt.
Dies verhindert unkontrollierte Abhängigkeiten zwischen Modulen.
Zusätzlich dazu können einzelne Pakete innerhalb eines Moduls als interface deklariert werden.
Nur dieses interface darf von anderen Modulen genutzt werden @springModulith.

Im folgenden Codebeispiel ist das `api` Paket des Moduls `parking-inventory` als interface deklariert.

Spring Modulith #footnote[org.springframework.modulith:spring-modulith-starter-test] ist ein offizielles Spring-Projekt, das speziell für die Entwicklung modularer Monolithen entwickelt wurde.
Es bietet Werkzeuge und Best Practices, um die Strukturierung, Kommunikation und Verwaltung von Modulen innerhalb eines Monolithen sicherzustellen.

Spring Modulith etabliert Regeln und Konventionen, die garantieren, dass Module klar abgegrenzt sind und nur über definierte Schnittstellen miteinander kommunizieren.
Standardmäßig dürfen Module nur auf Code zugreifen, der in der eigentlichen Paketstruktur des eigenen Moduls liegt.
Auf diese Weise werden unkontrollierte Abhängigkeiten zwischen Modulen verhindert.

Darüber hinaus können einzelne Pakete innerhalb eines Moduls explizit als Interface deklariert werden.
Nur dieses Interface darf von anderen Modulen verwendet werden, wodurch eine klar definierte Kommunikationsschicht entsteht @springModulith.

Im folgenden Codebeispiel ist das api-Paket des Moduls parking-inventory als Interface deklariert:

```kotlin
package com.spruhs.parkflow.parkinginventory.api

import org.springframework.modulith.NamedInterface
import org.springframework.modulith.PackageInfo

@PackageInfo
@NamedInterface(name = ["parking-inventory-api"])
class ModuleMetaData
```

Spring Modulith bietet zusätzlich die Möglichkeit, die Einhaltung der Modulgrenzen automatisiert zu testen.
Ein entsprechender Test#footnote[com.spruhs.parkflow.architecture.ModulithTests.kt] kann in den Build-Prozess eingebunden werden, um sicherzustellen, dass die modulare Struktur während der Entwicklung konsistent bleibt:

```kotlin
class ModulithTests {
    @Test
    fun `verifies modular structure`() {
        val modules = ApplicationModules.of(ParkFlowApplication::class.java)
        modules.verify()
    }
}
```

=== Archunit

ArchUnit ist ein Werkzeug zur automatisierten Überprüfung von Architekturregeln in Java- und Kotlin-Projekten.
Es ermöglicht, vordefinierte Architekturprinzipien und Schichtgrenzen programmatisch zu überprüfen.
Dadurch lassen sich Verstöße gegen die gewünschte Struktur frühzeitig erkennen, bevor sie zu Wartungsproblemen oder unerwarteten Abhängigkeiten führen.

In Parkflow wird ArchUnit insbesondere verwendet, um die hexagonale Architektur durchzusetzen.
Die Architekturregeln definieren klare Grenzen zwischen den Schichten domain, application und infrastructure.
Mit ArchUnit lassen sich diese Regeln automatisiert testen, sodass beispielsweise sichergestellt wird, dass:

- Die Domain-Schicht keine Abhängigkeiten auf Application oder Adapter hat,
- Die Application-Schicht keine Abhängigkeiten auf Adapter hat,
- Events und andere fachliche Klassen in den korrekten Paketen liegen,
- Namenskonventionen eingehalten werden, um Konsistenz und Verständlichkeit des Codes zu fördern.

Die Vorteile von ArchUnit liegen darin, dass die Einhaltung der Architektur kontinuierlich geprüft werden kann.
Die Tests#footnote[com.spruhs.parkflow.architecture.HexagonalArchitectureTests.kt] können in den Build-Prozess integriert werden, sodass neue Änderungen nur akzeptiert werden, wenn sie die definierten Architekturregeln nicht verletzen.
Auf diese Weise unterstützt ArchUnit die Modularität, Kohäsion und Trennung von Verantwortlichkeiten, die für modulare DDD-Architekturen zentral sind.

Durch den Einsatz von ArchUnit wird also nicht nur die Struktur des Codes dokumentiert, sondern auch aktiv durchgesetzt, was die Wartbarkeit, Lesbarkeit und langfristige Konsistenz des Systems unterstützt.

```kotlin
class HexagonalArchitectureTests {
    private val basePackage = "com.spruhs.parkflow"
    private val importedClasses = ClassFileImporter().importPackages(basePackage)

    @Test
    fun `domain should not depend on application or adapter`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..adapter..")
            .check(importedClasses)
    }

    @Test
    fun `application should not depend on adapter`() {
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAPackage("..adapter..")
            .check(importedClasses)
    }

    @ParameterizedTest(name = "{index}: {0} should reside in {1} package")
    @MethodSource("allowedNaming")
    fun `allowed naming rules`(
        naming: String,
        packageName: String,
    ) {
        classes()
            .that().haveSimpleNameEndingWith(naming)
            .should().resideInAPackage(packageName)
            .check(importedClasses)
    }

    companion object {
        @JvmStatic
        fun allowedNaming(): Stream<Arguments> =
            Stream.of(
                Arguments.of("Aggregate", "..domain.."),
                Arguments.of("Repository", "..infrastructure.secondary.."),
                Arguments.of("Adapter", "..infrastructure.."),
                Arguments.of("Port", "..application.."),
                Arguments.of("Command", "..application.."),
                Arguments.of("Message", "..infrastructure.primary.."),
                Arguments.of("Request", "..infrastructure.primary.."),
                Arguments.of("Projection", "..domain.."),
            )
    }
}
```
== Event Sourcing

In diesem Kapitel wird die Implementierung des Event Stores in Parkflow erläutert.
Der Event Store ist ein zentrales Element eines Event-Sourcing-Systems, da er alle Events speichert, die notwendig sind, um den Zustand von Aggregates zu rekonstruieren.

Der Event Store in Parkflow soll dabei folgende grundlegende Eigenschaften erfüllen:

- Er ist im Wesentlichen eine Liste von Events, in der jeder Eintrag ein einzelnes Event darstellt.
- Neue Events werden einfach an das Ende der Liste angehängt.
- Bereits gespeicherte Events werden niemals verändert oder gelöscht
@stack2022[p.~10].

Das Ziel dieser Implementierung ist es, einen generischen Mechanismus zu schaffen, der die folgenden Anforderungen unterstützt:

1. Speicherung von Events für verschiedene Aggregate-Typen.
2. Abruf von Events für ein bestimmtes Aggregate in der korrekten Reihenfolge.
3. Rekonstruktion des Aggregate-Zustands durch Anwendung der gespeicherten Events.
4. Transaktionssicherheit, sodass mehrere Events atomar gespeichert werden können.
5. Asynchrone Verarbeitung von Events, um die Skalierbarkeit und Performance des Systems zu verbessern.
6. Veröffentlichung von Events innerhalb der Anwendung, damit andere Module oder Komponenten auf Änderungen reagieren können.
7. Snapshot-Mechanismus, um die Wiederherstellung von Aggregates zu beschleunigen und die Anzahl der zu verarbeitenden Events zu reduzieren.

Die konkrete Implementierung befindet sich im Repository unter EventSourcing.kt#footnote[com.spruhs.parkflow.common.es.EventSourcing.kt].

=== Events

Um Event Sourcing in Parkflow umzusetzen, werden verschiedene Arten von Events benötigt, da unterschiedliche Teile der Anwendung unterschiedliche Anforderungen an Events haben.

Die Events dienen als reine Datencontainer und besitzen selbst keine fachliche Logik. Abbildung @event zeigt die Struktur der beiden Event-Klassen.

*BaseEvent*
Die Klasse BaseEvent wird für die interne Kommunikation innerhalb des Systems verwendet.
Sie ist abstrakt und wird von allen Events erweitert, die innerhalb des Systems erzeugt und verteilt werden.

BaseEvent enthält lediglich die Attribute, die für die Verarbeitung durch ein Aggregate notwendig sind:

- *aggregateId*: Die eindeutige Identifikationsnummer des Aggregates, zu dem das Event gehört.
- *metaData*: Zusätzliche (optionale) Metadaten zum Event.

Die BaseEvent-Klasse stellt somit sicher, dass Daten konsistent zwischen Modulen und Komponenten verteilt werden können.

*Persistiertes Event*

Für die Speicherung im Event Store wird die BaseEvent-Klasse erweitert und um weitere Attribute angereichert.
Diese zusätzlichen Informationen sind notwendig, um Events eindeutig zu identifizieren, zu versionieren, zu serialisieren und wiederherstellen zu können.
Persistierte Events bilden somit die Grundlage, um den Zustand eines Aggregates anhand der gespeicherten Events wiederherzustellen.
Dabei müssen die Events in der gleichen Reihenfolge verarbeitet werden, in der sie erzeugt wurden @stack2022[p.~102].

Die wichtigsten Attribute eines persistierten Events sind:

- *id*: Eindeutige Identifikationsnummer des Events.
- *type*: Typ des Events, der angibt, welche Art von Ereignis es ist (z.B. GateCreatedEvent).
- *aggregateId*: Identifikationsnummer des Aggregates, zu dem das Event gehört.
- *aggregateType*: Typ des Aggregates (z.B. Gate).
- *version*: Versionsnummer des Events, die angibt, in welcher Reihenfolge Events für ein bestimmtes Aggregate auftreten. Sie wird bei jedem neuen Event für dasselbe Aggregate inkrementiert.
- *data*: Die eigentlichen Event-Daten, als ByteArray gespeichert. Hier werden die Informationen aus BaseEvent serialisiert.
- *metaData*: Optionale Metadaten, die zusätzliche Informationen zum Event enthalten.
- *timestamp*: Zeitstempel, der angibt, wann das Event erstellt wurde.

Die Trennung zwischen BaseEvent und persistiertem Event ermöglicht es, dass Events innerhalb der Anwendung leicht verteilt werden können, während sie gleichzeitig vollständig für die dauerhafte Speicherung und spätere Wiederherstellung eines Aggregates vorbereitet sind.

#figure(
  image("./doc/eventsourcing/Event-0.svg"),
  caption: [
    Event und BaseEvent Klassen
  ],
) <event>

=== AggregateRoot

Die AggregateRoot-Klasse ist die Basisklasse für alle Aggregates in Parkflow, die den Event-Sourcing-Mechanismus nutzen.
Sie stellt die grundlegenden Funktionen bereit, die jedes Aggregate benötigt, um Events zu verwalten und den Zustand wiederherzustellen.
Die Klasse ist in Abbildung @aggregate-root dargestellt.
Die AggregateRoot Klasse hat folgende Attribute:

- *aggregateId*: Eindeutige Identifikationsnummer des Aggregates.
- *aggregateType*: Typ des Aggregates z.B. Gate.
- *changes*: Liste von BaseEvent-Objekten, die die Änderungen (Events) repräsentieren, die während der Lebensdauer des Aggregates aufgetreten sind.
- *version*: Aktuelle Versionsnummer des Aggregates, die angibt, wie viele Events bereits angewendet wurden.

*Nutzung der AggregateRoot-Klasse*

Ich beschreibe zunächst den Ablauf für ein bereits existierendes Aggregate, das bereits im Event Store gespeichert ist und mehrere Zustandsänderungen durchlaufen hat.

1. *Abrufen der Events*: Zuerst werden alle Events des Aggregates aus dem Event Store abgerufen. Die Events werden anhand ihrer Versionsnummer sortiert.
2. *Erstellen der Aggregate-Instanz*: Anschließend wird eine neue Instanz des Aggregates erzeugt.
3. *Anwenden der Events*: Jedes Event wird nacheinander auf das Aggregate angewendet. Dies geschieht über die Methode `raiseEvent`, die intern die abstrakte Methode `whenEvent` aufruft.

    - `whenEvent` muss von jeder konkreten Aggregate-Klasse implementiert werden.
    - In dieser Methode wird die fachliche Logik implementiert, die den Zustand des Aggregates basierend auf dem Event anpasst.

4. *Wiederhergestellter Zustand*: Nach der Anwendung aller Events spiegelt das Aggregate den aktuellen Zustand wider und kann in der Anwendung verwendet werden.

Erzeugen neuer Events

Während der Verwendung des Aggregates können neue Events erzeugt werden, die den Zustand weiter ändern.

- Neue Events werden über die Methode `apply` hinzugefügt.
- `apply` fügt das Event zur Liste `changes` hinzu, inkrementiert die Versionsnummer des Aggregates und ruft ebenfalls `whenEvent` auf, um den Zustand anzupassen.
- Die in `changes` gespeicherten Events können später im Event Store persistiert werden.
- Nach erfolgreichem Speichern wird die Liste der Änderungen über `clearChanges` geleert.

#figure(
  image("./doc/eventsourcing/AggregateRoot-0.svg"),
  caption: [
    AggregateRoot
  ],
) <aggregate-root>

=== Snapshot

Snapshots sind eine Optimierungstechnik im Event Sourcing, die dazu dient, die Wiederherstellung des Zustands eines Aggregates zu beschleunigen.
Anstatt bei jedem Laden eines Aggregates alle Events von Anfang an anzuwenden, wird der Zustand des Aggregates zu einem bestimmten Zeitpunkt als Snapshot gespeichert.

Ein Snapshot stellt eine Momentaufnahme des Aggregates dar, die den aktuellen Zustand vollständig repräsentiert.

Beim Wiederherstellen eines Aggregates wird zuerst der letzte Snapshot geladen. Danach werden nur noch die Events angewendet, die nach dem Snapshot aufgetreten sind.
Dadurch kann die Ladezeit deutlich reduziert werden, insbesondere wenn viele Events vorhanden sind.

Snapshots eignen sich nur zur Optimierung des Ladevorgangs und nicht zum Abfragen von Aggregates in einem bestimmten historischen Zustand. Sie ersetzen nicht die Events, sondern dienen lediglich als Ausgangspunkt für eine schnellere Rekonstruktion.

In Parkflow existiert pro Aggregate immer nur ein Snapshot.

- Wird ein neuer Snapshot erstellt, überschreibt er den alten Snapshot oder wird alternativ als neuer Eintrag gespeichert, während ältere Versionen verworfen werden.
- Dadurch wird sichergestellt, dass nur der aktuellste Zustand für die Optimierung genutzt wird.

Der Snapshot in Parkflow ist in Abbildung @snapshot dargestellt. Es handelt sich um eine Datenklasse, die folgende Attribute enthält:

- *id*: Eindeutige Identifikationsnummer für den Snapshot.
- *aggregateId*: Identifikationsnummer des Aggregates, zu dem der Snapshot gehört.
- *aggregateType*: Typ des Aggregates, zu dem der Snapshot gehört z.B. Gate.
- *data*: Serialisierte Darstellung des Aggregates zum Zeitpunkt des Snapshots, als ByteArray gespeichert.
- *metaData*: Optionale zusätzliche Metadaten, ebenfalls als ByteArray.
- *version*: Versionsnummer des Snapshots, die angibt, bis zu welchem Event der Snapshot den Zustand abbildet.
- *timestamp*: Zeitpunkt der Erstellung des Snapshots.


#figure(
  image("./doc/eventsourcing/Snapshot-0.svg"),
  caption: [
    Snapshot
  ],
) <snapshot>

=== PostgreSQL

Für die persistente Speicherung der Events und Snapshots wird in Parkflow eine PostgreSQL-Datenbank genutzt.

Um die PostgreSQL-Datenbank mit der Anwendung zu verbinden, wird der Spring Starter für R2DBC verwendet #footnote[org.springframework.boot:spring-boot-starter-data-r2dbc].
R2DBC (Reactive Relational Database Connectivity) erlaubt reaktive, nicht-blockierende Datenbankzugriffe, wodurch die Anwendung skalierbar bleibt und Lastspitzen besser abgefangen werden können @springR2dbc.

Für die Schema-Migration kommt Flyway zum Einsatz #footnote[org.flywaydb:flyway-core].
Beim Start der Anwendung prüft Flyway automatisch, ob die Datenbank auf dem aktuellen Stand ist, und führt ggf. notwendige Migrationen durch @flyway.
Wenn noch kein Schema vorhanden ist, führt Flyway die Migration `V1__initial_setup.sql` #footnote[park-flow/src/main/resources/db/migration/V1\_\_initial_setup.sql] aus, die die Tabellen für Events und Snapshots erstellt.

Die Events-Tabelle wird zusätzlich partitioniert, um die Performance bei großen Datenmengen zu verbessern.

- Partitionierung erfolgt auf Basis des Attributs `aggregate_id`.
- Alle Events eines Aggregates liegen somit in derselben Partition.
- Beim Abrufen eines Aggregates müssen nur die relevanten Partitionen gelesen werden, was die Abfragezeit reduziert.

Für die Events- und Snapshots-Tabelle werden außerdem Indexe auf `aggregate_id` und `version` erstellt, um schnelle Abfragen nach Aggregate und chronologischer Reihenfolge zu gewährleisten.

Die Tabellenstruktur in PostgreSQL ermöglicht damit eine einfache und effiziente Umsetzung eines Event-Sourcing-Systems, das sowohl vollständig konsistent als auch leistungsfähig ist.

```sql
CREATE TABLE IF NOT EXISTS parkflow.events
(
    event_id       VARCHAR(250) NOT NULL CHECK ( event_id <> '' ),
    aggregate_id   VARCHAR(250) NOT NULL CHECK ( aggregate_id <> '' ),
    aggregate_type VARCHAR(250) NOT NULL CHECK ( aggregate_type <> '' ),
    event_type     VARCHAR(250) NOT NULL CHECK ( event_type <> '' ),
    data           BYTEA,
    metadata       BYTEA,
    version        SERIAL       NOT NULL,
    timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (event_id, aggregate_id)
    ) PARTITION BY HASH (aggregate_id);

CREATE INDEX IF NOT EXISTS aggregate_id_aggregate_version_idx ON parkflow.events USING btree (aggregate_id, version ASC);

CREATE TABLE IF NOT EXISTS events_partition_hash_1 PARTITION OF parkflow.events FOR VALUES WITH (MODULUS 3, REMAINDER 0);

CREATE TABLE IF NOT EXISTS events_partition_hash_2 PARTITION OF parkflow.events FOR VALUES WITH (MODULUS 3, REMAINDER 1);

CREATE TABLE IF NOT EXISTS events_partition_hash_3 PARTITION OF parkflow.events FOR VALUES WITH (MODULUS 3, REMAINDER 2);

CREATE TABLE IF NOT EXISTS parkflow.snapshots
(
    snapshot_id    UUID PRIMARY KEY         ,
    aggregate_id   VARCHAR(250) UNIQUE NOT NULL CHECK ( aggregate_id <> '' ),
    aggregate_type VARCHAR(250)        NOT NULL CHECK ( aggregate_type <> '' ),
    data           BYTEA,
    metadata       BYTEA,
    version        SERIAL              NOT NULL,
    timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                 UNIQUE (aggregate_id)
    );

CREATE INDEX IF NOT EXISTS aggregate_id_aggregate_version_idx ON parkflow.snapshots USING btree (aggregate_id, version);
```

=== Aggregate Store

Das Herzstück des Event-Sourcing-Mechanismus in Parkflow ist der Aggregate Store.
Er ist verantwortlich für das Laden und Speichern von Aggregates, die Verwaltung von Snapshots sowie die Veröffentlichung von Events innerhalb der Anwendung.
Durch den Aggregate Store wird sichergestellt, dass die Event-Sourcing-Logik zentral gebündelt ist und die Integrität der Aggregate-Zustände gewahrt bleibt.
Die Struktur des Aggregate Stores ist in @aggregate-store dargestellt.

#figure(
  image("./doc/eventsourcing/AggregateStore-0.svg"),
  caption: [
    Aggregate Store
  ],
) <aggregate-store>


*Schnittstelle `AggregateStore`:*

Das Interface `AggregateStore` definiert die Kernfunktionen:

- Speichern und Laden von Events: `saveEvents`, `loadEvents`.
- Speichern und Laden von Aggregates: `save`, `load`.

```kotlin
interface AggregateStore {
    suspend fun saveEvents(events: List<Event>)

    suspend fun loadEvents(
        aggregateId: String,
        version: Int,
    ): MutableIterable<Event>

    suspend fun <T : AggregateRoot> save(aggregate: T)

    suspend fun <T : AggregateRoot> load(
        aggregateId: String,
        aggregateType: Class<T>,
    ): T
}
```

Alle Methoden sind `suspend` Funktionen, was bedeutet, dass sie asynchron innerhalb von Kotlin-Coroutines ausgeführt werden.
Eine suspend Funktion blockiert den aktuellen Thread nicht, wenn auf eine länger dauernde Operation gewartet wird.
Stattdessen pausiert die Coroutine, die die Funktion aufgerufen hat, und der Thread kann andere Aufgaben ausführen.
Sobald die Operation abgeschlossen ist, wird die Coroutine an der unterbrochenen Stelle wieder aufgenommen.

Auf einem System-Thread können mehrere Coroutines gleichzeitig ausgeführt bzw. gebündelt werden.
Die Koordination übernimmt dabei ein Coroutine Dispatcher, der die Coroutines auf die verfügbaren Threads verteilt.
Durch diese Mechanismen entsteht echte Nebenläufigkeit, ohne dass für jede Aufgabe ein eigener Thread benötigt wird.

suspend in Kombination mit Coroutines und einem Dispatcher ermöglicht nicht-blockierende, asynchrone Verarbeitung mit Nebenläufigkeit, wodurch Skalierbarkeit und Performance der Anwendung deutlich verbessert werden @coroutinesBasics.

*Implementierung `AggregateStoreImpl`:*

Die konkrete Implementierung `AggregateStoreImpl` übernimmt die Logik, um Events und Aggregates effizient zu persistieren und wiederherzustellen.
Wichtige Bestandteile:

- *DatabaseClient (R2DBC)*: Für asynchrone, nicht-blockierende Datenbankzugriffe.
- *TransactionalOperator*: Verwaltung von Transaktionen, sodass mehrere Datenbankoperationen atomar ausgeführt werden.
- *EventPublisher*: Veröffentlichung von Events innerhalb der Anwendung.
- *SerializerFactory*: Serialisierung und Deserialisierung von Events.
- *snapshotInterval*: Gibt an, nach wie vielen Events ein Snapshot erstellt werden soll.

*Speicher und Laden von Events*

Das Speichern und Laden von Events und Snapshot ist sehr ähnlich.
Es wird hier nur das Speichern und Laden von Events beschrieben.
Die Speicherung von Events erfolgt in einem reaktiven, transaktionalen Kontext, der sicherstellt, dass alle Operationen atomar durchgeführt werden.
Vor dem Speichern wird ein Lock auf das Aggregate gesetzt, um konkurrierende Schreibzugriffe zu verhindern.

```kotlin

override suspend fun saveEvents(events: List<Event>) {
    return events.forEach { saveEvent(it) }
}

private suspend fun saveEvent(event: Event) {
    return dbClient.sql(SAVE_EVENT_QUERY)
        .bind(EventSourcingConstants.EVENT_ID, event.id ?: "")
        .bind(EventSourcingConstants.AGGREGATE_ID, event.aggregateId)
        .bind(EventSourcingConstants.AGGREGATE_TYPE, event.aggregateType)
        .bind(EventSourcingConstants.EVENT_TYPE, event.type)
        .bind(EventSourcingConstants.VERSION, event.version)
        .bind(EventSourcingConstants.DATA, event.data)
        .bind(EventSourcingConstants.METADATA, event.metadata)
        .bind(EventSourcingConstants.TIMESTAMP, event.timeStamp)
        .await()
}

private const val SAVE_EVENT_QUERY = """
    INSERT INTO parkflow.events
        (event_id, aggregate_id, aggregate_type, event_type, data, metadata, version, timestamp)
    VALUES
        (:event_id, :aggregate_id, :aggregate_type, :event_type, :data, :metadata, :version, :timestamp)
"""

override suspend fun loadEvents(
    aggregateId: String,
    version: Int,
): MutableIterable<Event> {
    return withContext(Dispatchers.IO) {
        dbClient.sql(LOAD_EVENTS_QUERY)
            .bind(EventSourcingConstants.AGGREGATE_ID, aggregateId)
            .bind(EventSourcingConstants.VERSION, version)
            .map { row, meta -> eventFromRow(row, meta) }
            .all()
            .toIterable()
    }
}

private fun eventFromRow(
    row: Row,
    meta: RowMetadata,
) = Event(
        type = row.get(EventSourcingConstants.EVENT_TYPE, String::class.java) ?: "",
        aggregateId = row.get(EventSourcingConstants.AGGREGATE_ID, String::class.java) ?: "",
        aggregateType = row.get(EventSourcingConstants.AGGREGATE_TYPE, String::class.java) ?: "",
        id = row.get(EventSourcingConstants.EVENT_ID, String::class.java) ?: "",
        version = row.get(EventSourcingConstants.VERSION, Int::class.java) ?: 0,
        data = row.get(EventSourcingConstants.DATA, ByteArray::class.java) ?: byteArrayOf(),
        metadata = row.get(EventSourcingConstants.METADATA, ByteArray::class.java) ?: byteArrayOf(),
        timeStamp = row.get(EventSourcingConstants.TIMESTAMP, LocalDateTime::class.java) ?: LocalDateTime.now(),
    )

private const val LOAD_EVENTS_QUERY = """
    SELECT event_id, aggregate_id, aggregate_type, event_type, data, metadata, version, timestamp
    FROM parkflow.events e
    WHERE e.aggregate_id = :aggregate_id
      AND e.version > :version
    ORDER BY e.version ASC
"""
```

*Speichern von Aggregates:*

Beim Speichern eines Aggregates über die Methode `save` im Aggregate Store werden die folgenden Schritte ausgeführt:

1. *Auswahl des Serializers*: Zuerst wird über die SerializerFactory der passende Serializer für den Typ des Aggregates ausgewählt. Jeder Aggregate-Typ hat einen eigenen Serializer, der dafür zuständig ist, die BaseEvents in ein persistierbares Event-Objekt zu transformieren.

2. *Umwandlung der BaseEvents*: Alle Änderungen, die im Aggregate seit der letzten Speicherung aufgetreten sind, liegen in der changes-Liste als BaseEvents vor. Diese BaseEvents werden mithilfe des Serializers in persistierbare Events umgewandelt.

3. *Reaktiver Transaktionskontext*: Die Speicherung erfolgt innerhalb eines reaktiven Transactional Operators. Dieser Operator stellt sicher, dass alle Operationen atomar ausgeführt werden. Der gesamte Ablauf wird in einer Coroutine ausgeführt, wodurch die Datenbankzugriffe asynchron und nicht-blockierend erfolgen.

4. *Concurrency Handling*: Vor dem Schreiben der Events wird ein Lock auf das Aggregate gesetzt, um konkurrierende Schreibzugriffe zu verhindern. Dies geschieht über ein SQL-Statement, das die entsprechende Aggregate-ID für Updates sperrt. Damit wird sichergestellt, dass die Versionierung der Events korrekt bleibt und keine Race Conditions auftreten.

5. *Speichern der Events*: Anschließend werden die Events nacheinander in die Events-Tabelle in PostgreSQL geschrieben. Jede Einfügung ist asynchron und erfolgt über den DatabaseClient von R2DBC. Die Reihenfolge der Events wird durch die Versionsnummer sichergestellt.

6. *Snapshot-Erstellung*: Nach dem Speichern prüft der Aggregate Store, ob ein Snapshot erstellt werden soll.
    - Wenn ja, wird der aktuelle Zustand des Aggregates serialisiert und in der Snapshots-Tabelle gespeichert.
    - Falls bereits ein Snapshot existiert, wird dieser überschrieben, sodass immer nur ein Snapshot pro Aggregate existiert.


7. *Veröffentlichung der Events*: Alle neuen Events werden über den EventPublisher innerhalb der Anwendung veröffentlicht.

8. *Leeren der changes-Liste*: Zum Schluss wird die changes-Liste im Aggregate geleert. Dadurch wird sichergestellt, dass die gleichen Events nicht erneut gespeichert oder veröffentlicht werden.

Dieser Ablauf stellt sicher, dass die Events konsistent gespeichert, die Versionierung korrekt gehandhabt und gleichzeitig die Verarbeitung asynchron und entkoppelt bleibt.
Gleichzeitig erlaubt die Kombination aus Snapshots, Coroutine-basiertem asynchronem Speichern und Event-Publishing, dass der Aggregate Store performant und skalierbar arbeitet, selbst wenn viele Aggregates gleichzeitig gespeichert werden.

```kotlin
override suspend fun <T : AggregateRoot> save(aggregate: T) {
    val serializer = serializerFactory
                        .getSerializer(aggregate::class.java.simpleName)

    val events = aggregate.changes.map { serializer.serialize(it, aggregate) }
    operator.executeAndAwait {
        if (aggregate.version > 1) handleConcurrency(aggregate.aggregateId)

        saveEvents(events)

        if (aggregate.version % snapshotFrequency == 0) saveSnapshot(aggregate)

        eventPublisher.publish(
            aggregate.changes.filter { !it.metadata.imported }
        )

        aggregate.clearChanges()
    }
}

private suspend fun handleConcurrency(aggregateId: String) {
    dbClient.sql(HANDLE_CONCURRENCY_QUERY)
        .bind(EventSourcingConstants.AGGREGATE_ID, aggregateId)
        .await()
}

private const val HANDLE_CONCURRENCY_QUERY = """
    SELECT aggregate_id
    FROM parkflow.events
    WHERE aggregate_id = :aggregate_id
    ORDER BY version
    LIMIT 1
    FOR UPDATE
"""
```

*Laden von Aggregates:*

Beim Laden eines Aggregates über die Methode `load` im Aggregate Store werden folgende Schritte ausgeführt:

1. *Auswahl des Serializers*: Zuerst wird über die SerializerFactory der passende Serializer für den Typ des Aggregates ausgewählt.

2. *Laden des Snapshots*: Der Aggregate Store prüft, ob für das Aggregate ein Snapshot existiert.

    - Wenn ein Snapshot vorhanden ist, wird dieser geladen und das Aggregate daraus teilweise wiederhergestellt, sodass nicht alle Events von Anfang an angewendet werden müssen.
    - Wenn kein Snapshot existiert, wird eine neue leere Instanz des Aggregates erstellt.

3. *Anwendung der Events nach dem Snapshot*: Alle Events, die nach der Versionsnummer des geladenen Snapshots entstanden sind, werden aus der Events-Tabelle geladen.

    - Die Events werden nach der Versionsnummer aufsteigend sortiert, um die korrekte Reihenfolge sicherzustellen.
    - Jedes Event wird nacheinander auf das Aggregate angewendet, indem die Methode `raiseEvent` aufgerufen wird.
    - `raiseEvent` ruft intern die abstrakte Methode `whenEvent` auf, die von jeder konkreten Aggregate-Klasse implementiert wird, um den Zustand des Aggregates basierend auf dem Event anzupassen.

4. *Rückgabe des Aggregates*: Nach dem Anwenden aller Events ist der Zustand des Aggregates vollständig wiederhergestellt. Das Aggregate kann nun in der Anwendung genutzt werden.

```kotlin
override suspend fun <T : AggregateRoot> load(
    aggregateId: String,
    aggregateType: Class<T>,
): T {
    val serializer = serializerFactory
                        .getSerializer(aggregateType.simpleName)

    val snapshot = loadSnapshot(aggregateId)
    val aggregate = getAggregateFromSnapshotClass(snapshot, aggregateId, aggregateType)

    loadEvents(aggregateId, aggregate.version)
        .map { serializer.deserialize(it) }
        .forEach { aggregate.raiseEvent(it) }

    if (aggregate.version == 0) {
        throw AggregateNotFoundException(aggregateId, aggregateType.name)
    }

    return aggregate
}

private suspend fun <T : AggregateRoot> getAggregateFromSnapshotClass(
    snapshot: Snapshot?,
    aggregateId: String,
    aggregateType: Class<T>,
): T {
    if (snapshot == null) {
        val defaultSnapshot =
            EventSourcingUtils.snapshotFromAggregate(
                aggregate = getAggregate(aggregateId, aggregateType)
            )

        return EventSourcingUtils.getAggregateFromSnapshot(
            defaultSnapshot, aggregateType
        )
    }

    return EventSourcingUtils.getAggregateFromSnapshot(
        snapshot, aggregateType
    )
}
```

=== Serializer

In einem Event-Sourcing-System wie Parkflow müssen Events persistiert und später wiederhergestellt werden.
Dazu werden die Events in ein bytebasiertes Format serialisiert, das in der Datenbank gespeichert werden kann.
Beim Laden der Events müssen sie anschließend wieder deserialisiert werden, um die BaseEvent-Objekte im Speicher rekonstruieren zu können.

Um diese Aufgabe zu erfüllen, gibt es für jeden Aggregate-Typ einen eigenen Serializer.
Jeder Serializer kennt die spezifischen Event-Typen seines Aggregates und weiß, wie diese korrekt serialisiert und deserialisiert werden.
Die Verwaltung der Serializer übernimmt die SerializerFactory, die den passenden Serializer für einen bestimmten Aggregate-Typ bereitstellt:

```kotlin
@Component
class SerializerFactory(
    private val serializer: List<Serializer>,
) {
    fun getSerializer(aggregateType: String): Serializer {
        return serializer.firstOrNull {
            it.aggregateTypeName() == aggregateType
        } ?: throw IllegalArgumentException("Unknown aggregate type: $aggregateType")
    }
}
```

Jeder konkrete Serializer implementiert das Interface `Serializer`.
Dieses Interface definiert die drei zentralen Funktionen.

```kotlin
interface Serializer {
    fun serialize(
        event: BaseEvent,
        aggregate: AggregateRoot,
    ): Event

    fun deserialize(event: Event): BaseEvent

    fun aggregateTypeName(): String
}
```

Als Beispiel für einen konkreten Serializer wird hier der GateEventSerializer dargestellt.
Dieses Beispiel zeigt, wie der Serializer die spezifischen Events des Gate Aggregates behandelt.

```kotlin
enum class GateEvent {
    GATE_CREATED_V1,
    GATE_ACTIVATED_V1,
    GATE_DEACTIVATED_V1,
    GATE_REMOVED_V1,
}
```
- Jedes Event des Aggregates bekommt eine Version (z.B. `_V1`), damit zukünftige Änderungen an den Events ohne Probleme umgesetzt werden können.
- Durch die Versionsierung können alte Events weiterhin korrekt deserialisiert werden, selbst wenn die Struktur der Events angepasst wird.


Der `GateEventSerializer`#footnote[com.spruhs.parkflow.parkinginventory.api.ParkingInventoryEvents.kt] implementiert das Interface `Serializer`.

```kotlin
@Component
class GateEventSerializer : Serializer {
    private val typeMapping: Map<Class<out BaseEvent>, GateEvent> =
        mapOf(
            GateCreatedEvent::class.java to GateEvent.GATE_CREATED_V1,
            GateActivatedEvent::class.java to GateEvent.GATE_ACTIVATED_V1,
            GateDeactivatedEvent::class.java to GateEvent.GATE_DEACTIVATED_V1,
            GateRemovedEvent::class.java to GateEvent.GATE_REMOVED_V1,
        )

    private val classMapping: Map<String, Class<out BaseEvent>> =
        typeMapping.entries.associateBy(
            { it.value.name },
            { it.key },
        )

    override fun serialize(
        event: BaseEvent,
        aggregate: AggregateRoot,
    ): Event { ... }

    override fun deserialize(event: Event): BaseEvent { ... }

    override fun aggregateTypeName(): String = "GateAggregate"
}
```

=== Event Publisher

Im Event-Sourcing-System von Parkflow müssen Änderungen an Aggregates nicht nur im Event Store persistiert werden, sondern auch innerhalb der Anwendung kommuniziert werden, damit andere Komponenten auf diese Änderungen reagieren können.

Dafür wird das Interface `EventPublisher`#footnote[com.spruhs.parkflow.common.es.Events.kt] genutzt:

```kotlin
fun interface EventPublisher {
    fun publish(events: List<BaseEvent>)
}
```

Die konkrete Implementierung erfolgt in der Klasse `EventPublisherImpl`.
Diese nutzt den von Spring bereitgestellten `ApplicationEventPublisher`, um die Events innerhalb der Anwendung zu verteilen.

```kotlin
@Service
class EventPublisherImpl(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventMetrics: EventMetrics,
) : EventPublisher {

    override fun publish(events: List<BaseEvent>) {
        events.forEach {
            eventMetrics.springPublished.increment()

            applicationEventPublisher.publishEvent(it)
        }
    }
}
```

== Beispiel: Gate Aggregate

In diesem Kapitel wird die Implementierung des Gate-Aggregats sowie der zugehörigen Events, Ports, Adapter und Projektionen erläutert.
Das Gate-Aggregat dient dabei exemplarisch zur Darstellung der Aggregate, die in den Modulen ParkingInventory und CustomerAccess verwendet werden.

Die strukturelle Ausgestaltung sowie die bei der Implementierung auftretenden Herausforderungen sind bei allen betrachteten Aggregaten vergleichbar.
Ziel dieses Kapitels ist es, ein Aggregat, das im Rahmen der Modellierung mittels Event Storming innerhalb der jeweiligen Bounded Contexts identifiziert wurde, in Code zu überführen und für die weitere Nutzung innerhalb der Anwendung bereitzustellen.

Die übergeordnete Architektur des Moduls wurde bereits in Abschnitt @code anhand eines C4-Diagramms dargestellt. Das Gate-Aggregat wird im Folgenden als konkretes Beispiel innerhalb dieser Architektur betrachtet.

=== Aggregate

Die Implementierung beginnt im Kern der Anwendung mit der Definition des Aggregates im Domain Layer#footnote[com.spruhs.parkflow.parkinginventory.core.domain.Gate.kt].
Das Gate-Aggregat stellt dabei die zentrale fachliche Einheit dar und kapselt sowohl den Zustand als auch das Verhalten des Gates.

Das Gate-Aggregat erbt von der abstrakten Klasse `AggregateRoot` und überschreibt das Attribut `aggregateId` sowie die Methode `whenEvent`.
Der `aggregateType` wird über eine Konstante definiert, um eine eindeutige Typisierung des Aggregates zu ermöglichen.
Anschließend werden die Attribute definiert, die den aktuellen Zustand des Gates repräsentieren:

- *gateType*: Value Object, das den Typ des Gates beschreibt (Entrance, Exit) und im API-Modul definiert ist.
- *name*: Value Object, das den Namen des Gates repräsentiert. Die Validierung des Namens erfolgt innerhalb des Value Objects selbst.
- *activationState*: Value Object, das den Aktivierungszustand des Gates beschreibt (Activated, Deactivated).
- *removed*: Boolesches Attribut, das angibt, ob das Gate als entfernt markiert wurde. Beim Entfernen eines Gates bleibt dieses im System erhalten, um die Historie der Zustandsänderungen nachvollziehen zu können. Dieses Vorgehen wird als Soft Delete bezeichnet.

Im nächsten Schritt werden die Commands als Methoden definiert, die vom Gate-Aggregat verarbeitet werden können.
Das CreateGateCommand wird dabei als statische Methode implementiert, da zum Zeitpunkt der Erstellung noch keine Instanz des Aggregates existiert.

An dieser Stelle wird die wesentliche Logik der Command-Verarbeitung implementiert#footnote[Das Gate-Aggregat enthält vergleichsweise wenig fachliche Logik. Andere Aggregate der Anwendung besitzen eine umfangreichere Logik, werden jedoch aus Gründen der Übersichtlichkeit an dieser Stelle nicht näher dargestellt.].
Innerhalb der Command-Methoden wird unter anderem geprüft, ob das Gate bereits als entfernt markiert wurde, um die Ausführung weiterer Zustandsänderungen zu verhindern.

Führt die Ausführung eines Commands zu einer Zustandsänderung des Gates, wird ein entsprechendes Domain Event erzeugt und über die Methode `apply` dem Aggregat hinzugefügt.
Die `apply`-Methode speichert das Event zunächst in der Liste der noch nicht persistierten Änderungen und ruft anschließend die Methode `whenEvent` auf, um den internen Zustand des Aggregates zu aktualisieren.

Die Methode `whenEvent` ist so implementiert, dass die unterschiedlichen Event-Typen verarbeitet und die zugehörigen Zustandsänderungen am Aggregat vorgenommen werden.
Beim Speichern des Aggregates im Event Store werden schließlich die gesammelten Events persistiert und veröffentlicht.

```kotlin
class GateAggregate(
    override val aggregateId: String
) : AggregateRoot(aggregateId, TYPE) {
    var gateType: GateType = GateType.ENTRANCE
    var name: GateName = GateName("DEFAULT")
    var activationState: ActivationState = ActivationState.ACTIVE
    var removed: Boolean = false

    override fun whenEvent(event: BaseEvent) {
        when (event) {
            is GateCreatedEvent -> handleGateCreatedEvent(event)
            is GateActivatedEvent -> {
                this.activationState = ActivationState.ACTIVE
            }
            is GateDeactivatedEvent -> {
                this.activationState = ActivationState.INACTIVE
            }
            is GateRemovedEvent -> this.removed = true

            else -> throw UnknownEventTypeException(event)
        }
    }

    private fun handleGateCreatedEvent(event: GateCreatedEvent) {
        this.name = event.name
        this.gateType = event.gateType
    }

    private fun ensureNotRemoved() {
        require(!removed) {
            "ParkingSpot has been removed and cannot accept commands anymore."
        }
    }

    fun activate() {
        ensureNotRemoved()
        if (activationState == ActivationState.ACTIVE) return

        apply(GateActivatedEvent(aggregateId))
    }

    fun deactivate() {
        ensureNotRemoved()
        if (activationState == ActivationState.INACTIVE) return

        apply(GateDeactivatedEvent(aggregateId))
    }

    fun remove() {
        ensureNotRemoved()

        apply(GateRemovedEvent(aggregateId))
    }

    companion object {
        const val TYPE = "Gate"

        fun create(
            gateType: GateType,
            name: GateName,
        ) = GateAggregate(generateId())
            .also { it.apply(GateCreatedEvent(it.aggregateId, gateType, name)) }
    }
}
```

=== Projection

Ebenfalls im Domain Layer sind die Projektionen verortet.
Die Gate-Events sind Bestandteil der ParkingInventory-Projektion#footnote[com.spruhs.parkflow.parkinginventory.core.domain.ParkingInventory.kt].

Projektionen dienen dazu, den aktuellen Zustand von Read Models auf Basis der eingehenden Events abzubilden.
Die ParkingInventory-Projektion verwaltet den aktuellen Zustand aller Gates und Parkplätze innerhalb des Systems.

Dabei enthalten Projektionen möglichst wenig fachliche Logik und sind primär dafür zuständig, den aktuellen Zustand der Read Models zu speichern und für Abfragen bereitzustellen.

```kotlin
data class ParkingInventoryProjection(
    val gates: MutableList<GateProjection> = mutableListOf(),
    val parkingSpots: MutableList<ParkingSpotProjection> = mutableListOf(),
)

data class GateProjection(
    val gateId: String,
    val name: String,
    val type: GateType,
    val state: ActivationState = ActivationState.ACTIVE,
)

data class ParkingSpotProjection(
    ...
)
```

=== UseCases

Um die Commands des Gate-Aggregats von außen aufrufen zu können, werden im Application Layer sogenannte Use-Case-Ports definiert#footnote[package com.spruhs.parkflow.parkinginventory.core.application.GateUseCases].
Diese Ports werden anschließend durch entsprechende Adapter implementiert.

Für jeden Command des Gate-Aggregats wird eine Methode im jeweiligen Use-Case-Port definiert.
Der Ablauf innerhalb der Use Cases ist dabei weitgehend einheitlich.
Zunächst wird das entsprechende Aggregat über den Aggregate Store geladen.
Anschließend wird die jeweilige Command-Methode auf dem Aggregat ausgeführt.
Zum Abschluss wird das Aggregat erneut im Aggregate Store gespeichert, wobei die erzeugten Events persistiert und veröffentlicht werden.

Neben dieser grundlegenden Orchestrierung übernehmen die Use Cases zusätzliche Aufgaben, wie beispielsweise das Laden weiterer benötigter Ressourcen oder die Validierung von Zuständen, die nicht innerhalb des Aggregates selbst überprüft werden können.
Insgesamt übernehmen die Use Cases somit die Koordination der beteiligten Komponenten zur Verarbeitung der Commands.

Im Kontext der Gate Use Cases ergeben sich zwei zentrale Herausforderungen, die adressiert werden müssen:

1. Beim Ausführen von Commands kann es zu konkurrierenden Schreibzugriffen auf dasselbe Gate kommen, wenn mehrere Commands gleichzeitig auf ein Aggregat angewendet werden. Dies kann potenziell zu Inkonsistenzen im Zustand des Gates führen.
2. Beim Erstellen neuer Gates muss sichergestellt werden, dass der Name eines Gates eindeutig ist. Da Gate-Aggregate keinen globalen Systemzustand kennen, kann diese Prüfung nicht innerhalb des Aggregates erfolgen. Stattdessen wird hierfür auf Projektionen zurückgegriffen. Aufgrund der eventual consistency der Projektionen ergibt sich jedoch die Herausforderung, dass ein Name mehrfach vergeben werden könnte, wenn ein Command bereits ausgeführt wurde, die Projektion jedoch noch nicht aktualisiert ist.

Die erste Herausforderung wird durch den Einsatz eines Mutex aus dem Paket kotlinx.coroutines adressiert.
Ein Mutex ermöglicht es, kritische Abschnitte so zu schützen, dass jeweils nur ein Command gleichzeitig auf ein bestimmtes Gate zugreifen kann.
Auf dieser Grundlage wird ein generischer Lock-Mechanismus implementiert, der das Laden und Speichern von Aggregaten kapselt#footnote[com.spruhs.parkflow.common.helper.KeyedMutex.kt].

Die Klasse KeyedMutex verwaltet eine ConcurrentHashMap, in der für jeden Schlüssel, in diesem Fall die aggregateId, ein eigener Mutex gespeichert wird.
Über die Methode withKeyLock kann ein kritischer Abschnitt definiert werden.
Die Methode erhält einen Schlüssel sowie einen auszuführenden Block als Parameter.
Zunächst wird der zugehörige Mutex aus der Map gelesen oder neu erzeugt.
Anschließend wird der Block innerhalb eines durch den Mutex geschützten Abschnitts ausgeführt.
Nach Abschluss der Ausführung wird der Mutex aus der Map entfernt.

Auf diese Weise wird sichergestellt, dass jeweils nur ein Command gleichzeitig auf dasselbe Aggregat zugreifen kann.
Gleichzeitig erlaubt dieser Ansatz die nebenläufige Verarbeitung von Commands für unterschiedliche Aggregate, ohne diese gegenseitig zu blockieren.

```kotlin
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class KeyedMutex<K> {
    private val mutexes = ConcurrentHashMap<K, Mutex>()

    suspend fun <T> withKeyLock(
        key: K,
        block: suspend () -> T,
    ): T {
        val mutex = mutexes.computeIfAbsent(key) { Mutex() }

        return try {
            mutex.withLock {
                block()
            }
        } finally {
            mutexes.remove(key, mutex)
        }
    }
}
```

Die zweite Herausforderung wird mithilfe des `ParkingInventoryService` adressiert#footnote[Der Service wird auch für weitere Aufgaben verwendet. Im Folgenden wird jedoch ausschließlich die Reservierung von Gate-Namen betrachtet. com.spruhs.parkflow.parkinginventory.core.application.ParkingInventoryService.kt].
Der Service stellt Methoden zur Abfrage und Aktualisierung der ParkingInventory-Projektion bereit.

Zur Sicherstellung der Eindeutigkeit von Gate-Namen bietet der Service die Methode `reserveGateName` an.
Hierzu verwaltet der Service eine interne Map, in der reservierte Gate-Namen zusammen mit einem Zeitstempel gespeichert werden.
Bei einer Reservierung wird zunächst geprüft, ob der Name bereits in der Map enthalten ist oder in der Projektion existiert.
Ist dies nicht der Fall, wird der Name zusammen mit dem aktuellen Zeitstempel in der Map abgelegt.

Sobald ein Gate erfolgreich erstellt wurde, wird der Service über das `GateCreatedEvent` informiert und der reservierte Name aus der Map entfernt.
Zusätzlich enthält der Service eine mit `@Scheduled` annotierte Methode des Spring Frameworks, die in regelmäßigen Intervallen abgelaufene Reservierungen entfernt.
Hierdurch wird verhindert, dass nicht abgeschlossene Reservierungen dauerhaft im System verbleiben.

Da die Reservierung des Gate-Namens vor der eigentlichen Erstellung erfolgt, kann sichergestellt werden, dass Gate-Namen auch bei parallel ausgeführten Commands und verzögerter Aktualisierung der Projektion nicht mehrfach vergeben werden.
Der `ParkingInventoryService` ist als Spring Service implementiert und existiert innerhalb der Anwendung als Singleton, wodurch die Verwaltung der reservierten Namen zentral erfolgt.

```kotlin
@Service
class ParkingInventoryService(private val repository: ParkingInventoryRepositoryPort) {

    ...

    private val reservedGateNames: MutableMap<GateName, Instant> = mutableMapOf()

    @Scheduled(fixedRate = 60 * 1000)
        private fun cleanupExpiredReservations() {
            val now = Instant.now()
            reservedGateNames.entries.removeIf {
                (_, reservedAt) -> isReservationTimeOver(reservedAt, now)
            }

            reservedParkingSpotNames.entries.removeIf {
                (_, reservedAt) -> isReservationTimeOver(reservedAt, now)
            }
        }

    private fun isReservationTimeOver(
        reservedAt: Instant,
        now: Instant,
    ) = Duration.between(reservedAt, now)
            .toMinutes() > RESERVATION_TIME_IN_MINUTES

    suspend fun reserveGateName(name: GateName) {
        require(name !in reservedGateNames.keys) { "Gate name already exists" }
        require(!repository.existsGateName(name)) { "Gate name already exists" }

        reservedGateNames[name] = Instant.now()
    }

    suspend fun handleGateCreatedEvent(event: GateCreatedEvent) {
        repository.save(event.toProjection())

        reservedGateNames.remove(event.name)
    }

    ...
}
```

=== REST-Adapter

Die REST-Adapter des Gate-Aggregats befinden sich im Infrastructure Layer des ParkingInventory-Moduls#footnote[com.spruhs.parkflow.parkinginventory.core.infrastructure.primary.GateRest.kt].

Zur Implementierung der Adapter wird der Spring Boot Starter für WebFlux verwendet#footnote[org.springframework.boot:spring-boot-starter-webflux], wodurch reaktive, asynchrone und nicht-blockierende REST-Endpunkte bereitgestellt werden.
Die Endpunkte nutzen Kotlin Coroutines und sind daher als `suspend`-Funktionen implementiert.

Die REST-Adapter fungieren als Primary Adapter und stellen HTTP-Endpunkte zur Ausführung von Commands bereit.
Jeder Endpunkt delegiert eingehende Anfragen an die entsprechendenvGate-UseCases, die über den `GateCommandPort` angebunden sind.
Der Adapter selbst enthält keine Business-Logik, sondern ist ausschließlich für Request-Mapping und Weiterleitung zuständig.

```kotlin
@RestController
@RequestMapping("/api/v1/parking-inventory/gates")
class GateRestAdapter(private val commandPort: GateCommandPort) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createGate(
        @RequestBody body: CreateGateRequest,
    ) = commandPort.create(body.toCommand())

    @PostMapping("/{gateId}/activation-state")
    suspend fun updateActivationState(
        @PathVariable gateId: String,
        @RequestParam state: String,
    ) = when (ActivationState.valueOf(state)) {
        ActivationState.ACTIVE -> commandPort.activate(GateId(gateId))
        ActivationState.INACTIVE -> commandPort.deactivate(GateId(gateId))
    }

    @DeleteMapping("/{gateId}")
    suspend fun removeGate(
        @PathVariable gateId: String,
    ) = commandPort.remove(GateId(gateId))
}
```

=== Event Listener Adapter

Die Event Listener Adapter befinden sich im Infrastructure Layer des Moduls.
Sie sind nicht ausschließlich für das Gate-Aggregat konzipiert, können jedoch Events des Gate-Aggregats verarbeiten.

Im ParkingInventory-Modul gibt es einen Listener für die Aktualisierung der ParkingInventory#footnote[com.spruhs.parkflow.parkinginventory.core.infrastructure.primary.ParkingInventoryListenerAdapter.kt].

Die Methoden werden mit der Spring-Annotation `@EventListener` für die verschiedenen Event-Typen registriert, die der Listener verarbeiten kann.
Jedes Event wird anschließend an einen Port weitergeleitet (`ParkingInventoryCommandPort`), der die eigentliche Verarbeitung übernimmt.
Zur asynchronen Verarbeitung wird für jedes Event eine neue Kotlin Coroutine gestartet.

Der Adapter selbst enthält keine Business-Logik, sondern übernimmt ausschließlich die Orchestrierung und Delegation der Events.

```kotlin
@Component("parkingInventoryInventoryListenerAdapter")
class ParkingInventoryListenerAdapter(
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val commandPort: ParkingInventoryCommandPort,
) {
    @EventListener(
        ParkingSpotCreatedEvent::class,
        ParkingSpotRemovedEvent::class,
        ParkingSpotTypesRemovedEvent::class,
        ParkingSpotTypesAddedEvent::class,
        ParkingSpotRenamedEvent::class,
        ParkingSpotActivatedEvent::class,
        ParkingSpotDeactivatedEvent::class,
        GateCreatedEvent::class,
        GateActivatedEvent::class,
        GateDeactivatedEvent::class,
        GateRemovedEvent::class,
    )
    fun onEvent(event: BaseEvent) {
        eventExecutionStrategy.execute {
            commandPort.handleEvent(event)
        }
    }
}
```

=== MongoDB Adapter

Die Projektionen werden in einer MongoDB-Datenbank gespeichert.
Hierfür wird der Spring Boot Starter für MongoDB Reactive#footnote[org.springframework.boot:spring-boot-starter-data-mongodb-reactive] verwendet, der eine reaktive und nicht-blockierende Implementierung des MongoDB-Treibers bereitstellt.

Für die ParkingInventory-Projektion wird ein Interface im Infrastructure Layer erstellt#footnote[com.spruhs.parkflow.parkinginventory.core.application.ParkingInventoryService.kt].
Dieses Interface fungiert als Port für die Projektion und definiert die Methoden zum Abfragen, Speichern und Entfernen von Gates und ParkingSpots.

```kotlin
interface ParkingInventoryRepositoryPort {
    suspend fun getInventory(): ParkingInventoryProjection

    suspend fun getGate(gateId: String): GateProjection?

    suspend fun getParkingSpot(parkingSpotId: String): ParkingSpotProjection?

    suspend fun save(gateProjection: GateProjection)

    suspend fun save(parkingSpotProjection: ParkingSpotProjection)

    suspend fun existsGateName(name: GateName): Boolean

    suspend fun existsParkingSpotName(name: ParkingSpotName): Boolean

    suspend fun removeParkingSpot(parkingSpotId: String)

    suspend fun removeGate(gateId: String)
}
```

Dieser Port wird im Infrastructure Layer von einem Adapter implementiert#footnote[com.spruhs.parkflow.parkinginventory.core.infrastructure.secondary.ParkingInventoryMongoDB.kt].
Der Adapter verwendet das Spring Data MongoDB Reactive Repository, um die Projektionen asynchron zu speichern und abzurufen.
Aus Performancegründen werden Gates und ParkingSpots in separaten Collections gespeichert, wodurch gezielte Abfragen effizienter möglich sind.

```kotlin
@Service
class ParkingInventoryRepositoryAdapter(
    private val gateRepository: GateRepository,
    private val parkingSpotRepository: ParkingSpotRepository,
) : ParkingInventoryRepositoryPort {
    override suspend fun getInventory() =
        ParkingInventoryProjection(
            gates = gateRepository.findAll()
                                  .map { it.toProjection() }
                                  .collectList()
                                  .awaitSingle(),

            parkingSpots = parkingSpotRepository.findAll()
                                                .map { it.toProjection() }
                                                .collectList()
                                                .awaitSingle(),
        )

    override suspend fun getGate(gateId: String) =
        gateRepository.findById(gateId)
            .awaitSingleOrNull()
            ?.toProjection()

    override suspend fun getParkingSpot(parkingSpotId: String) =
        parkingSpotRepository.findById(parkingSpotId)
            .awaitSingleOrNull()
            ?.toProjection()

    override suspend fun save(gateProjection: GateProjection) {
        gateRepository.save(gateProjection.toDocument()).awaitSingle()
    }

    override suspend fun save(parkingSpotProjection: ParkingSpotProjection) {
        parkingSpotRepository.save(parkingSpotProjection.toDocument())
                             .awaitSingle()
    }

    override suspend fun existsGateName(name: GateName) =
        gateRepository.existsByName(name.value).awaitSingle()

    override suspend fun existsParkingSpotName(name: ParkingSpotName) =
        parkingSpotRepository.existsByName(name.value).awaitSingle()

    override suspend fun removeParkingSpot(parkingSpotId: String) {
        parkingSpotRepository.deleteById(parkingSpotId).awaitSingleOrNull()
    }

    override suspend fun removeGate(gateId: String) {
        gateRepository.deleteById(gateId).awaitSingleOrNull()
    }
}

@Repository
interface GateRepository : ReactiveMongoRepository<GateDocument, String> {
    fun existsByName(name: String): Mono<Boolean>
}

@Repository
interface ParkingSpotRepository : ReactiveMongoRepository<ParkingSpotDocument, String> {
    fun existsByName(name: String): Mono<Boolean>
}
```

== Beispiel: ParkingOperation

In diesem Kapitel wird die Implementierung des ParkingOperation-Bounded-Contexts erläutert.
Wie im Kapitel @subdomains-chapter herausgearbeitet, handelt es sich hierbei um die Core Domain der Anwendung.

Die ParkingOperation-Domain ist dafür zuständig, Parkvorgänge in Echtzeit zu verwalten und die entsprechenden Events zu verarbeiten.
Dies stellt besondere Anforderungen an die Implementierung, insbesondere hinsichtlich der Konsistenz des aktuellen Parkstatus und der asynchronen Eventverarbeitung.

Die Darstellung folgt dem bekannten Aufbau.
 Ausgehend vom Kern der Domain, dem Aggregate, werden zunächst die Use Cases und Ports beschrieben, bevor die Infrastructure Layer Adapter vorgestellt werden.

=== ParkingOperator Aggregate

Das `ParkingOperatorAggregate` befindet sich im Domain Layer des ParkingOperation-Moduls#footnote[com.spruhs.parkflow.parkingoperation.core.domain.ParkingOperator.kt].
Es verwaltet den aktuellen Zustand aller Parkvorgänge im Parkhaus.

Das Aggregate erbt von der Klasse `AggregateRoot` und überschreibt die Attribute `aggregateId` sowie die Methode `whenEvent`.
Es definiert verschiedene Attribute, die den Zustand des ParkingOperators repräsentieren:

- *parkingSpots*: Eine Map, die alle Parkplätze verwaltet. Schlüssel ist `ParkingSpotId`, Wert ist ein Value Object `ParkingSpot`.
- *gates*: Eine Map aller Gates. Schlüssel ist `GateId`, Wert ist ein Value Object `Gate`.
- *vehicles*: Eine Map aller Fahrzeuge im Parkhaus. Schlüssel ist `PlateNumber`, Wert ist ein Value Object `Vehicle`.
- *parkingSpotProvider*: Ein Interface zur Verfügungstellung verschiedener Strategien, um passende Parkplätze zuzuweisen. Die Standardstrategie prüft zunächst auf passende Typen und weist ansonsten den ersten verfügbaren Platz zu.

```kotlin
class ParkingOperatorAggregate(override val aggregateId: String) : AggregateRoot(aggregateId, TYPE) {
    private val log = getLogger(javaClass)

    val parkingSpots: MutableMap<ParkingSpotId, ParkingSpot> = mutableMapOf()
    val gates: MutableMap<GateId, Gate> = mutableMapOf()
    val vehicles: MutableMap<PlateNumber, Vehicle> = mutableMapOf()

    private var parkingSpotProvider: ParkingSpotProvider = DefaultParkingSpotProvider()

    ...
}
```

Die Value Objects `ParkingSpot`, `Gate` und `Vehicle` modellieren die dynamische Nutzung des Parkhauses im ParkingOperation Bounded Context.
Im Unterschied zu den gleichnamigen Objekten in anderen Contexts, die primär der Verwaltung dienen, stehen hier die Echtzeitinformationen und Zustandsänderungen im Vordergrund.
Dies folgt dem DDD-Prinzip der Kontextabhängigen Modellierung.
Gleichartige Subjekte übernehmen in verschiedenen Bounded Contexts unterschiedliche Rollen, was Abhängigkeiten zwischen den Contexts minimiert.

ParkingSpot repräsentiert einen Parkplatz in Echtzeit.
Neben den grundlegenden Informationen über Typ und Status enthält es Attribute für aktuell parkende Fahrzeuge, Reservierungen und temporäre Vermietungen.
Dadurch kann der Aggregate jederzeit den belegten oder freien Zustand jedes Parkplatzes ermitteln und Parkvorgänge korrekt steuern.

```kotlin
data class ParkingSpot(
    val parkingSpotId: ParkingSpotId,
    val types: MutableSet<ParkingSpotType> = mutableSetOf(),
    var parkingVehicle: PlateNumber? = null,
    var reservedForVehicle: PlateNumber? = null,
    var isActive: Boolean = true,
    var rental: Rental? = null,
) {
    fun isRented(): Boolean {
        if (rental == null) {
            return false
        }

        val today = LocalDate.now()

        if (today.isBefore(rental?.from)) return false

        if (rental?.to != null && today.isAfter(rental?.to)) return false

        if (rental?.to == null && today.isAfter(rental?.from)) return false

        return true
    }
}

data class Rental(
    val plateNumber: PlateNumber,
    val from: LocalDate,
    val to: LocalDate? = null,
)
```

Gate modelliert Ein- und Ausfahrten des Parkhauses.
Je nach Typ (`Entrance` oder `Exit`) beeinflusst es die Bewegung von Fahrzeugen und löst Events aus, die den Aggregate-Zustand dynamisch anpassen.

```kotlin
sealed class Gate(open val gateId: GateId, open var isActive: Boolean = true) {
    data class Exit(override val gateId: GateId, override var isActive: Boolean = true) : Gate(gateId, isActive)

    data class Entrance(override val gateId: GateId, override var isActive: Boolean = true) : Gate(gateId, isActive)
}
```


Vehicle repräsentiert ein Fahrzeug innerhalb des Parkhauses.
Das Value Object hält den aktuellen Zustand (`DrivingAround`, `OnGate`, `OnParkingSpot`) fest und ermöglicht dem Aggregate, das Verhalten jedes Fahrzeugs in Echtzeit nachzuvollziehen.

```kotlin
data class Vehicle(
    val plateNumber: PlateNumber,
    val hasDisabilityCard: Boolean = false,
    var state: VehicleAction,
)

sealed class VehicleAction {
    object DrivingAround : VehicleAction()

    data class OnGate(val gate: Gate) : VehicleAction()

    data class OnParkingSpot(val parkingSpotId: ParkingSpotId) : VehicleAction()
}
```

Um die parkingSpots und gates zu verwalten verarbeitet der ParkingOperator verschiedene Events vom ParkingInventory Bounded Context.
Hierbei entsteht das Problem, dass die Events beim Speichern automatisch veröffentlicht werden.
Das darf aber nicht passieren, da der ParkingOperator fachlich gesehen kein Gate oder ParkingSpot erstellt, sondern nur den aktuellen Zustand verwaltet.
Aus diesem Grund werden diese Events als imported Events markiert.

Die Parkvorgänge werden durch Events repräsentiert wie z.B. VehicleArrived.
Diese kommen von den Sensoren und werden vom ParkingOperator verarbeitet um den Zustand der Fahrzeuge zu verwalten.
Wie in @bounded-contexts beschrieben, werden die Commands für den ParkingOperator über Events ausgelöst.
Der ParkingOperator stellt dabei Methoden zur Verfügung um die verschiedenen Events zu verarbeiten.

Insgesamt werden vier Methoden zur Verarbeitung von Events zur Verfügung gestellt.

Die `onVehicleArrival` Methode verarbeitet, wenn ein Fahrzeug an einem Gate ankommt.
Die Methode erstellt ein neues Vehicle Object für das Fahrzeug und weist ihm einen Parkplatz zu und gibt eine entsprechende `GateResponse` zurück.
Damit kann die Anwendung dann eine Entsprechende Aktion durchführen wie z.B. das Tor öffnen, den Zugewiesenen Parkplatz mitteilen oder eine Fehlermeldung mitteilen.
Bei einer State Veränderung des Aggregates wird ein entsprechendes Event erzeugt und über die apply Methode hinzugefügt.

```kotlin
fun onVehicleArrival(
    gateId: GateId,
    plateNumber: PlateNumber,
    hasDisabilityCard: Boolean,
): GateResponse {
    val gate = gates[gateId] ?: return GateResponse.Error.NotFoundError
    val arrivedVehicle = Vehicle(
        plateNumber,
        hasDisabilityCard,
        VehicleAction.OnGate(gate)
    )

    return determineArriveAction(gate, arrivedVehicle)
        .also {
            apply(VehicleArrivedEvent(aggregateId, gateId, arrivedVehicle))
        }
}

sealed class GateResponse {
    sealed class Error : GateResponse() {
        object PlateNumberNotRegisteredError : Error()

        object NoParkingSpotAvailableError : Error()

        object NotFoundError : Error()
    }

    sealed class Action : GateResponse() {
        data class ProvideParkingSpot(val parkingSpotId: ParkingSpotId) : Action()

        object LetVehicleOut : Action()
    }
```

Die weiteren Methoden verarbeiten das Durchfahren eines Gates, das Parken auf einem Parkplatz und das Verlassen eines Parkplatzes.
Auch diese Methoden erzeugen entsprechende Events bei einer Zustandsveränderung des Aggregates.
In der `whenEvent` Methode werden die Events verarbeitet und der Zustand des Aggregates angepasst.

```kotlin
fun onVehicleDroveThrough(
    gateId: GateId,
    plateNumber: PlateNumber,
) {
    when (gates[gateId] ?: return) {
        is Gate.Entrance ->
            apply(
                VehicleEnteredParkingLotEvent(
                    aggregateId,
                    gateId,
                    plateNumber,
                    vehicles[plateNumber]?.hasDisabilityCard ?: false,
                ),
            )

        is Gate.Exit -> apply(
            VehicleLeavedParkingLotEvent(aggregateId, gateId, plateNumber)
        )
    }
}

fun onVehicleParkedOn(
    parkingSpotId: ParkingSpotId,
    plateNumber: PlateNumber,
) {
    val parkingSpot = parkingSpots[parkingSpotId] ?: return
    if (parkingSpot.parkingVehicle != null) {
        log.error("CRASH $plateNumber on $parkingSpotId is already vehicle parked!")
    }

    if (parkingSpot.reservedForVehicle == plateNumber) {
        parkCorrect(parkingSpotId, plateNumber)
    } else {
        parkIncorrect(plateNumber, parkingSpot)
    }
}

fun onVehicleParkedOff(
    parkingSpotId: ParkingSpotId,
    plateNumber: PlateNumber,
) = apply(VehicleParkedOffEvent(aggregateId, parkingSpotId, plateNumber))
```

=== Parking Operator Service

Der `ParkingOperatorService` befindet sich im Application Layer des ParkingOperation-Moduls #footnote[com.spruhs.parkflow.parkingoperation.core.application.ParkingOperatorService.kt].
Er ist für die Verwaltung des `ParkingOperatorAggregate` verantwortlich.
Er lädt und speichert den Aggregate-Zustand, übergibt Events an den Aggregate und verarbeitet dessen Antworten.

Der Service ist als Singleton implementiert, da die Anwendung nur einen ParkingOperator benötigt.
Beim Start lädt der Service das Aggregate aus dem `AggregateStore` oder erstellt ein neues, falls noch keines existiert.

Um konkurrierende Schreibzugriffe auf das Aggregate zu verhindern, wird das Actor Pattern verwendet.
Der `ParkingOperatorActor` besitzt einen Channel, über den alle Aktionen sequentiell in einer eigenen Coroutine ausgeführt werden.
Die Methode `execute` ermöglicht es, eine Aktion in dem Channel einzureihen und auf dem Aggregate auszuführen, auf deren Ergebnis zu warten und anschließend den aktuellen Aggregate-Zustand zu speichern.
Nur die seit dem letzten Speichern hinzugekommenen Events werden veröffentlicht, sodass der Zustand in der Datenbank stets aktuell bleibt und Events in Echtzeit verarbeitet werden.

```kotlin
@Service
class ParkingOperatorService(
    private val store: AggregateStore,
    private val gateController: GateControllerPort,
    private val customerPort: CustomerOperationApiPort,
    private val notificationPort: CustomerNotificationPort,
    eventExecutionStrategy: EventExecutionStrategy,
) {
    ...

    private lateinit var actor: ParkingOperatorActor

    init {
        eventExecutionStrategy.execute {
            actor = loadParkingSpotOperator()
        }
    }

    private suspend fun loadParkingSpotOperator() =
        try {
            val aggregate = store.load(
                PARKING_SPOT_OPERATOR_AGGREGATE_ID,
                ParkingOperatorAggregate::class.java
            )
            ParkingOperatorActor(aggregate, store)
        } catch (_: AggregateNotFoundException) {
            ParkingOperatorActor(
                ParkingOperatorAggregate(PARKING_SPOT_OPERATOR_AGGREGATE_ID),
                store
            )
        }
    ...
}

class ParkingOperatorActor(
    private val aggregate: ParkingOperatorAggregate,
    private val aggregateStore: AggregateStore,
) {
    private val commandChannel = Channel<suspend () -> Unit>(Channel.UNLIMITED)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            for (cmd in commandChannel) {
                cmd()
            }
        }
    }

    suspend fun <T> execute(
        command: suspend ParkingOperatorAggregate.() -> T
    ): T {
        val deferred = CompletableDeferred<T>()
        commandChannel.send {
            try {
                val result = aggregate.command()
                aggregateStore.save(aggregate)
                deferred.complete(result)
            } catch (e: Throwable) {
                deferred.completeExceptionally(e)
            }
        }
        return deferred.await()
    }
}
```

Der Service nutzt verschiedene Ports, die von unterschiedlichen Adaptern implementiert werden können:

- *GateControllerPort*: Öffnet Gates, zeigt zugewiesene Parkplätze an
- *CustomerOperationApiPort*: Prüft, ob ein Fahrzeug registriert ist
- *CustomerNotificationPort*: Benachrichtigt Kunden über Parkplatzinformationen oder Fehler

Die Methode handleCarArrived verarbeitet den Ankunftsfall eines Fahrzeugs:

1. Überprüft die Registrierung des Fahrzeugs über die Customer API
2. Führt `onVehicleArrival` im Actor aus
3. Verarbeitet die Antwort (`GateResponse`) und löst entsprechende Aktionen aus:

    - Öffnen des Gates (`LetVehicleOut`)
    - Anzeigen des zugewiesenen Parkplatzes (`ProvideParkingSpot`)
    - Verarbeiten von Fehlermeldungen (`Error`)

Weitere Methoden verarbeiten ähnliche Aktionen, wie das Durchfahren eines Gates oder das Parken auf einem Parkplatz, und rufen dabei direkt die entsprechenden Methoden des Aggregates auf.

Durch den Einsatz des Actor Patterns und die sequentielle Verarbeitung der Befehle wird die Konsistenz des Aggregates sichergestellt, Events werden sauber veröffentlicht und die Core Domain kann die Parkvorgänge in Echtzeit verwalten.

```kotlin
suspend fun handleCarArrived(
    gateId: GateId,
    plateNumber: PlateNumber,
    hasDisabilityCard: Boolean,
) {
    if (!isPlateRegistered(plateNumber)) {
        gateController.showError(
            gateId,
            plateNumber,
            GateResponse.Error.PlateNumberNotRegisteredError
        )
        return
    }

    val response = actor.execute {
        onVehicleArrival(gateId, plateNumber, hasDisabilityCard)
    }

    handleGateResponse(response, gateId, plateNumber)
}

private suspend fun handleGateResponse(
    response: GateResponse,
    gateId: GateId,
    plateNumber: PlateNumber,
) {
    when (response) {
        is GateResponse.Action.LetVehicleOut -> {
            gateController.openGate(gateId, plateNumber)
        }
        is GateResponse.Action.ProvideParkingSpot -> {
            gateController.showProvidedParkingSpot(
                gateId,
                response.parkingSpotId,
                plateNumber
            )
        }

        is GateResponse.Error -> {
            gateController.showError(gateId, plateNumber, response)
        }
    }
}
```

=== Vehicle Sensor Adapter

Die Vehicle Sensor Adapter befinden sich im Infrastructure Layer des ParkingOperation-Moduls #footnote[com.spruhs.parkflow.parkingoperation.core.infrastructure.primary.VehicleEventListenerAdapter.kt].
Ihre Aufgabe ist es, die Events der Fahrzeuge, die über Sensoren erfasst und über RabbitMQ veröffentlicht werden, zu empfangen und an den Port weiterzuleiten.

Der Adapter verwendet den Spring Boot AMQP Starter #footnote[org.springframework.boot:spring-boot-starter-amqp] und überwacht mit der Annotation `@RabbitListener` die entsprechenden Queues.
Beim Empfang eines Events wird dieses in einer neuen Coroutine asynchron verarbeitet und über den `ParkingOperationCommandPort` weitergegeben.

```kotlin
@Service
class VehicleEventListenerAdapter(
    private val commandPort: ParkingOperationCommandPort,
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val metrics: EventMetrics,
) {
    private val log = getLogger(javaClass)

    @RabbitListener(queues = [QUEUE_ARRIVED])
    fun handleVehicleArrived(event: CarArrivedSensorEvent) {
        log.info("VehicleArrivedEvent received: $event")
        metrics.rabbitReceived.increment()
        eventExecutionStrategy.execute {
            commandPort.vehicleArrived(
                GateId(event.gateId),
                PlateNumber(event.plateNumber),
                event.hasDisabilityCard
            )
        }
    }

    ...

}
```

=== Zusammenfassung

In diesem Kapitel wurde die Umsetzung der ParkingOperation- und ParkingInventory-Bounded Contexts sowie der darunterliegenden Event-Sourcing-Mechanismen in Parkflow beschrieben.
Durch die Kombination von Spring Boot und Kotlin war es möglich, auf allen Ebenen asynchrone und nicht-blockierende Komponenten zu erstellen.
Von den REST- und Event-Adaptern über den Service bis hin zu den Aggregates selbst und schließlich auch der Persistenzschicht mit MongoDB Reactive und dem Event Store.

Herausforderungen wie konkurrierende Schreibzugriffe auf Aggregates und die Sicherstellung der Konsistenz der Projektionen konnten dabei mit einfachen Mitteln gelöst werden, die von Kotlin und Spring bereitgestellt werden.
So sorgt das Actor Pattern in Kombination mit Kotlin-Coroutines dafür, dass alle Aktionen auf dem Aggregate sequentiell abgearbeitet werden, ohne dass zusätzliche Synchronisationsmechanismen erforderlich sind.
Mit KeyedMutex wurde ein generischer Lock-Mechanismus implementiert, der konkurrierende Zugriffe auf einzelne Aggregate verhindert, ohne die Nebenläufigkeit für unterschiedliche Aggregate zu beeinträchtigen.
Und durch die Reservierung von Gate-Namen im ParkingInventoryService konnte die Eindeutigkeit von Gate-Namen trotz eventual consistency der Projektionen sichergestellt werden. Diese Architektur ermöglicht neben echter Nebenläufigkeit auch eine skalierbare Verarbeitung hoher Event-Raten.

Das Event-Sourcing-Prinzip wurde konsequent umgesetzt, indem alle Zustandsänderungen der Aggregate durch Events repräsentiert und persistiert werden.
Der dafür entwickelte Aggregate Store kapselt die Komplexität des Ladens und Speicherns von Aggregates und stellt der Anwendung eine einfache Schnittstelle bereit.

Die Verwendung von Maps an vielen Stellen der Implementierung hat sich als besonders effizient erwiesen, da so ein Zugriff auf die Objekte in O(1) möglich ist.
Dies ist insbesondere bei Echtzeit-Operationen und der Verarbeitung von Sensor-Events wichtig, um schnelle Reaktionen zu gewährleisten.

Die Aggregates selbst halten den konsistenten Zustand der Core Domain und führen alle Aktionen auf Grundlage ihrer eigenen Daten aus.
Daher ist es nicht zwingend erforderlich, dass die Read Models immer sofort aktualisiert werden.
Falls aktuelle Projektionen benötigt werden, stehen geeignete Mechanismen zur Verfügung, um dieses Problem zu lösen.
Dadurch ist eventual consistency in diesem Szenario akzeptabel und ermöglicht eine hohe Performance sowie Skalierbarkeit der Anwendung.

Insgesamt zeigt die Implementierung, dass sich mit Spring und Kotlin eine robuste, reaktive, skalierbare und konsistente Architektur umsetzen lässt, die sowohl die Anforderungen der Core Domain als auch die Integrität der Event-Sourcing-Infrastruktur zuverlässig abbildet.
Die Kombination aus nicht-blockierender Verarbeitung, Snapshot-Optimierung, Actor-basiertem Service und Event-Publishing ermöglicht eine performante Handhabung von Parkvorgängen – selbst unter hoher Last.

= Evaluierung

#bibliography("literatur.bib")

#pagebreak()
