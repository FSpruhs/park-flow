= Thema 1:
== Implementierung und Evaluierung eines Event-Sourcing-Ansatzes mit Spring Boot in einer modularen Architektur

=== Problem und Motivation

In modernen Softwaresystemen steigen die Anforderungen an Skalierbarkeit, Nachvollziehbarkeit von Zustandsänderungen und Erweiterbarkeit stetig. Traditionelle, zustandsorientierte Persistenzansätze stoßen dabei häufig an ihre Grenzen, insbesondere wenn Systeme wachsen oder in kleinere, unabhängige Komponenten zerlegt werden sollen. \

Der Event-Sourcing-Ansatz bietet hier eine alternative Perspektive: Anstatt den aktuellen Zustand einer Anwendung direkt zu speichern, werden alle Zustandsänderungen als Ereignisse persistiert. Der aktuelle Zustand kann somit jederzeit aus der Ereignishistorie rekonstruiert werden. Dies ermöglicht eine lückenlose Nachverfolgung von Änderungen, erleichtert Integrationen über Events und unterstützt architektonische Muster wie CQRS (Command Query Responsibility Segregation). \

Darüber hinaus entsteht durch das Speichern aller Ereignisse ein wertvoller Datenschatz, der weit über die reine Zustandsverwaltung hinausgeht. Die Ereignisdaten enthalten detaillierte Informationen über die Entwicklung und Nutzung eines Systems im Zeitverlauf. Solche historischen Daten können in der modernen Informationsverarbeitung vielfältig genutzt werden. Beispielsweise für Analysen, Prognosen oder Machine-Learning-Anwendungen. Event Sourcing schafft somit nicht nur technische Vorteile hinsichtlich Nachvollziehbarkeit und Modularität, sondern eröffnet auch neue Möglichkeiten zur Datenanalyse und Entscheidungsunterstützung. \

In Kombination mit einer modularen Architektur nach dem Domain-Driven Design (DDD) ergibt sich ein vielversprechender Ansatz für den Aufbau flexibler und wartbarer Systeme. Insbesondere das Konzept des Modulithen, einer klar strukturierten, modularen Monolith-Architektur, erlaubt die Umsetzung von DDD-Prinzipien, ohne sofort in die Komplexität einer verteilten Microservice-Architektur zu verfallen.

=== Zielsetzung der Arbeit

Ziel dieser Arbeit ist es, die Implementierung eines Event-Sourcing-Ansatzes innerhalb einer modularen Spring-Boot-Anwendung zu entwickeln und zu evaluieren.
Im Rahmen der Arbeit soll eine Beispielanwendung entstehen – beispielsweise ein vereinfachtes ERP-System oder ein Tool zur Verwaltung von Scrum-Teams – welches mehrere Bounded Contexts enthält (z. B. User Management, Project Management, Task Tracking). \

Dabei sollen folgende Teilziele erreicht werden:
- Konzeption und Implementierung einer modularen Architektur (Modulith) basierend auf den DDD-Prinzipien nach Eric Evans und Vaughn Vernon.
- Implementierung eines eigenen Event Stores, der Ereignisse speichert, versioniert und Zustände rekonstruieren kann.
- Anwendung des CQRS-Patterns, um separate Modelle für Lese- und Schreiboperationen zu definieren.
- Evaluierung des entwickelten Ansatzes hinsichtlich Verständlichkeit, Erweiterbarkeit, Performance und Testbarkeit.
- Analyse des entstehenden Ereignisdatensatzes im Hinblick auf dessen Potenzial für weiterführende Datenauswertungen und Erkenntnisgewinn.

= Thema 2:
== Vergleichende Implementierung von Event-getriebenen Microservices in Go und Kotlin/Spring Boot
=== Problemstellung und Motivation

Moderne Softwarearchitekturen entwickeln sich zunehmend hin zu ereignisgetriebenen Microservices, um Systeme besser skalieren, entkoppeln und flexibel erweitern zu können. In solchen Architekturen erfolgt die Kommunikation zwischen Services asynchron über Ereignisse, was eine höhere Ausfallsicherheit und geringere Kopplung ermöglicht. \

Die konkrete Umsetzung dieser Architekturprinzipien hängt jedoch stark von der verwendeten Programmiersprache, dem Framework und den Messaging-Technologien ab. Während Kotlin/Spring Boot im Enterprise-Umfeld als ausgereiftes Framework gilt, das eine große Bandbreite an Integrationsmöglichkeiten und eine enge Einbindung in das Spring-Ökosystem bietet, zeichnet sich Go durch seine hohe Performance, einfache Parallelisierungsmodelle und geringe Laufzeitkomplexität aus.\

Ein systematischer Vergleich dieser beiden Technologien im Kontext event-getriebener Microservices bietet die Möglichkeit, deren Stärken und Schwächen hinsichtlich Entwicklungsaufwand, Performance und Architekturqualität zu analysieren. Dadurch lassen sich praxisnahe Empfehlungen ableiten, welche Sprache und welches Framework sich für bestimmte Szenarien besser eignet. \

=== Zielsetzung der Arbeit
Ziel dieser Arbeit ist die vergleichende Untersuchung von Go und Kotlin/Spring Boot im Hinblick auf die Entwicklung event-getriebener Microservice-Architekturen.
Hierzu sollen zwei funktional äquivalente Systeme entwickelt werden, die über ein Messaging-System, beispielsweise Apache Kafka, NATS oder RabbitMQ, miteinander kommunizieren.\

Als Beispielanwendung bietet sich ein ereignisgetriebenes Bestellsystem an, das aus mehreren Microservices besteht, etwa:
- Order-Service: Erzeugt Bestellungen und sendet entsprechende Events.
- Payment-Service: Reagiert auf Order-Events und verarbeitet Zahlungen.
- Notification-Service: Sendet Benachrichtigungen, sobald eine Zahlung erfolgreich abgeschlossen wurde.

Dieses Szenario ermöglicht die Abbildung eines vollständigen Eventflusses mit mehreren unabhängigen Services und damit eine fundierte technische und architektonische Bewertung der beiden Ansätze. \

Folgende Teilziele sollen im Rahmen der Arbeit erreicht werden:
- Konzeption und Implementierung eines ereignisgetriebenen Microservice-Systems mit Go (z. B. unter Verwendung von Gin und NATS oder Kafka).
- Konzeption und Implementierung eines äquivalenten Systems mit Kotlin/Spring Boot (z. B. auf Basis von Spring Cloud Stream oder Kafka).
- Vergleich der beiden Implementierungen hinsichtlich Architektur, Code-Struktur, Entwicklungsaufwand und Testbarkeit.
- Messung und Bewertung der Performance, Skalierbarkeit und Ressourcenverwendung beider Systeme.

