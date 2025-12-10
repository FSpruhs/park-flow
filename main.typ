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
- *Event Sourcing*: n diesem Ansatz werden alle Änderungen des Systemzustands als eine chronologische Abfolge von Events persistiert. Der aktuelle Zustand kann jederzeit durch das erneute Abspielen dieser Events rekonstruiert werden. Dadurch entsteht ein vollständig nachvollziehbarer Verlauf aller Zustandsänderungen.
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

Es gibt verschiedene Architekturmuster, die bei der Umsetzung von Softwarelösungen verwendet werden können.
In dieser Arbeit werde ich mich auf den Modulithen konzentrieren.
Dabei handelt es sich um eine Architektur, die die Vorteile von Monolithen und Microservices miteinander kombiniert.

Zunächst stelle ich die beiden Architekturmuster Monolith und Microservices vor, um anschließend den Modulith als Zwischenform zu erläutern.
Darüber hinaus wird die hexagonale Architektur vorgestellt, die eine wichtige Rolle bei der internen Strukturierung von Softwarekomponenten spielt.

=== Monolith

Ein Monolith ist eine Softwareanwendung, die als eine einzige, zusammenhängende Einheit entwickelt, bereitgestellt und betrieben wird @köhler2025[p.~327].
Monolithen zeichnen sich durch ihre Einfachheit aus, da alle Komponenten und Funktionalitäten in einem einzigen Prozess laufen.

Vorteile von Monolithen sind:
- *Einfache Entwicklung und Bereitstellung*: Da alle Komponenten in einer einzigen Anwendung enthalten sind, ist die Entwicklung und das Deployment vergleichsweise unkompliziert.
- *Geringer Overhead*: Monolithen benötigen keine komplexe Infrastruktur für die Kommunikation zwischen verschiedenen Diensten, was den Overhead reduziert.
- *Einfache Tests*: Integrationstests können leichter durchgeführt werden, da alle Komponenten in einer einzigen Anwendung laufen.

Trotz ihrer Vorteile haben Monolithen auch gravierende Nachteile.
Insbesondere, wenn das System über die Jahre stark wächst oder viele Teams parallel daran arbeiten.

Ein Monolith ohne klare Modul- oder Domänenstruktur entwickelt sich häufig zu einem Big Ball of Mud @vernon2017[p.~16].
Typische Probleme sind:
- Komponenten referenzieren sich gegenseitig ohne klare Regeln.
- Änderungen an einer Stelle führen zu unerwarteten Seiteneffekten an anderen Stellen.
- Der gesamte Code wird schwerer zu verstehen und zu überblicken.

=== Microservices

Microservices sind ein Architekturmuster, bei dem eine Anwendung aus einer Sammlung kleiner, unabhängiger Dienste besteht.
Jeder Dienst ist für eine klar abgegrenzte Funktionalität verantwortlich und kommuniziert über wohldefinierte Schnittstellen mit anderen Diensten @distributed2023[p.~65–66].

Microservices lassen sich gut mit den Prinzipien des Domain-Driven Design kombinieren.
Ein Microservice bildet häufig einen oder mehrere Bounded Contexts ab.
Zudem kann jeder Service genau die Technologien, Datenbanken und Programmiersprachen verwenden, die für seine spezifische Aufgabe am besten geeignet sind @khononov2022[p.~255–256].

Darüber hinaus bieten Microservice noch weitere Vorteile.
Durch das unabhängige Deployment können einzelne Dienste unabhängig voneinander aktualisiert und skaliert werden.
Auch ist die Fehlertoleranz höher, da der Ausfall eines Dienstes nicht zwangsläufig das gesamte System beeinträchtigt.

Allerdings bringen Microservices auch Herausforderungen mit sich.
Die Komplexität der Infrastruktur steigt, da Dienste orchestriert und überwacht werden müssen.
Die Kommunikation zwischen den Microservices erfolgt zwangläufig über ein Netzwerk.

Damit einhergehen weitere Herausforderungen wie der Netzwerklatenz, der Fehlertoleranz, der Sicherheit und der Datenkonsistenz @distributed2023[p.~53].

=== Modulith

Ein Modulith ist ein Architekturmuster, das die Vorteile von Monolithen und Microservices vereint.
Die Anwendung wird als eine einzige Einheit bereitgestellt, ist jedoch intern in klar abgegrenzte Module unterteilt @stack2022[p.~41].

Vom Monolithen übernimmt der Modulith die einfache Bereitstellung und den geringen Overhead.
Durch die Modularisierung wird jedoch eine klare Struktur geschaffen, die die Wartbarkeit und Erweiterbarkeit und die Grenzen der Microservices wiederspiegelt.

Dabei gehen einige Nachteile von Microservices verloren, da keine komplexe Infrastruktur für die Kommunikation zwischen den Diensten erforderlich ist.
Die Module kommunizieren innerhalb des gleichen Prozesses, was die Latenz reduziert und die Fehlertoleranz erhöht.
Jedoch gehen auch einige Vorteile von Microservices verloren, wie die unabhängige Skalierbarkeit und das unabhängige Deployment der einzelnen Module.
Für Softwareprojekte, die eine moderate Komplexität aufweisen und bei denen die Vorteile von Microservices nicht zwingend erforderlich sind, stellt der Modulith eine attraktive Alternative dar.

Ein weiterer Vorteil von Modulithen ist, dass er mit geringen Aufwand in eine Microservice-Architektur überführt werden kann.
Somit können neue oder junge Projekte zunächst als Modulith gestartet und bei wachsender Komplexität später in Microservices aufgeteilt werden.
Die Teams haben dadurch die Möglichkeit, sich zunächst auf die fachlichen Anforderungen zu konzentrieren, ohne sich von Anfang an mit der Komplexität einer Microservice-Architektur auseinandersetzen zu müssen.

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
Bei der hexagonalen Architektur wird die Domänenlogik der Anwendung von äußeren Systemen und Schnittstellen isoliert. Die Domäne befindet sich dabei im Zentrum der Architektur und wird mit möglichst wenigen äußeren Abhängigkeiten modelliert.
Technische Aspekte werden nicht innerhalb der Domäne implementiert.

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

Ein Event repräsentiert somit einen Fakt, der tatsächlich eingetreten ist und nicht rückgängig gemacht werden kann. Man kann sich ein System, das auf Events basiert, als eine Art Fortschreiten der Zeit vorstellen: Jeder Event markiert einen Punkt in dieser Zeitachse und trägt zur Abfolge der Ereignisse bei. Dadurch lassen sich reale Sachverhalte realistischer nachvollziehen und die Software näher an der tatsächlichen Domäne entwickeln, wie es das Domain-Driven Design anstrebt. Änderungen in der Vergangenheit können nicht gelöscht werden; es ist lediglich möglich, kompensierende Handlungen durchzuführen, um unerwünschte Effekte zu korrigieren.

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
