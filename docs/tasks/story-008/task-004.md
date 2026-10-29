# Tutoriel 004 — Story #008 : Brancher Failsafe dans le `pom.xml`

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** aucun
**Exigence TP couverte :** prépare `EXG-COM-09` et `EXG-COM-10`
**Fichier principal :** `pom.xml`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Dire à Maven de lancer les tests `*IT` dans une phase **à part**, après l'emballage du JAR.

## 📋 Ce que tu vas apprendre

- Surefire et Failsafe : deux plugins, deux familles de tests
- Les phases Maven `test` → `package` → `integration-test` → `verify`
- Pourquoi Failsafe a **deux** objectifs (`integration-test` **et** `verify`)

---

## 🪜 Les étapes

### Étape 1 : Ajoute le plugin

**Où :** `pom.xml`, dans IntelliJ

**Action :**

> Dans `<build>` → `<plugins>`, juste **après** le bloc du `spring-boot-maven-plugin` (après sa
> balise `</plugin>`), ajoute :
>
> ```xml
>             <!-- Integration tests (*IT.java) run in the integration-test phase, against compose-test.yml -->
>             <plugin>
>                 <groupId>org.apache.maven.plugins</groupId>
>                 <artifactId>maven-failsafe-plugin</artifactId>
>                 <executions>
>                     <execution>
>                         <goals>
>                             <goal>integration-test</goal>
>                             <goal>verify</goal>
>                         </goals>
>                     </execution>
>                 </executions>
>             </plugin>
> ```
>
> **Enregistre**, puis clique **Load Maven Changes** si IntelliJ le propose.

**Résultat attendu :** `<plugins>` contient 2 plugins : `spring-boot-maven-plugin`, puis
`maven-failsafe-plugin`.

**Pourquoi pas de `<version>` ?** Le parent Spring Boot **gère** déjà la version de Failsafe (comme
celle de Surefire) : elle évolue avec Spring Boot, testée avec lui. Le scanner Sonar, lui, n'est pas
géré par le parent : c'est pour ça que tu l'avais épinglé.

**Pourquoi deux objectifs ?**

| Objectif | Phase | Ce qu'il fait |
|----------|-------|---------------|
| `integration-test` | `integration-test` | lance les `*IT` et **note** les échecs, sans arrêter le build |
| `verify` | `verify` | lit les résultats et **fait échouer** le build s'il y a eu un échec |

Cette séparation laisse une place **entre les deux** pour arrêter un serveur ou une base, même si les
tests ont échoué. Jenkins fera la même chose avec `post { always }` (story 012).

---

### Étape 2 : Vérifie que Maven voit le plugin

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B -q help:effective-pom -Doutput=/dev/stdout | grep -A1 '<artifactId>maven-failsafe-plugin' | head -2
> ```

**Résultat attendu :**

```
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>3.5.6</version>
```

(La version vient du parent Spring Boot.)

**✅ Vérifie que :**
- [ ] Une version s'affiche sous `maven-failsafe-plugin`

---

### Étape 3 : Les tests unitaires restent indépendants

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B test | grep -E 'Tests run:|BUILD'
> ```

**Résultat attendu :** toujours `Tests run: 9, Failures: 0`, puis `BUILD SUCCESS`. Aucun `*IT`.

**Pourquoi c'est important ?** La phase `test` s'arrête **avant** `integration-test` : un développeur
sans Docker (ou le stage « tests unitaires » de Jenkins) n'a pas besoin de la base.

---

## 🧹 Comment tout annuler

Retire le bloc ajouté à l'étape 1.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Non-parseable POM` | Le bloc est collé hors de `<plugins>`, ou une balise n'est pas fermée |
| L'étape 2 n'affiche rien | Le fichier n'est pas enregistré, ou l'`artifactId` est mal orthographié |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Failsafe visible | `help:effective-pom … \| grep …` | `maven-failsafe-plugin` + une version |
| Unitaires seuls | `./mvnw -B test` | `Tests run: 9`, sans `*IT` |

---

## 🎉 Bravo !

Tes tests ont maintenant **deux voies** : la rapide (unitaire) et la complète (intégration). 🎉

Prochain tutoriel : **#005** — Lancer les tests d'intégration et lire leurs rapports ⏱️ 20 min
