# Create Tour Flow

Diese Datei beschreibt den aktuellen `Create Tour`-Flow anhand eines konkreten Beispiels.

Beispielszenario:

- der User ist eingeloggt
- der User klickt auf `+ New Tour`
- der User gibt Tourdaten ein
- der User wechselt zu `Summary`
- der User kann zur Bearbeitung zurueck
- wenn der User den Flow verlassen will, erscheint ein Leave-Dialog

Der Fokus liegt dabei auf:

- `Reactive Forms`
- `signal` fuer den Draft zwischen den Wizard-Schritten
- `canDeactivate` Leave-Guard
- Zusammenspiel zwischen Guard, Dialog und Draft-Reset

## Ausgangspunkt: User klickt auf `+ New Tour`

Der Einstieg passiert aktuell ueber Dashboard oder Tours. Beide Buttons navigieren auf die Details-Seite:

- Route: `/tours/create/details`
- Seite: `CreateTourDetailsPage`

Die Route ist in [app.routes.ts](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/app.routes.ts) definiert:

```ts
{
  path: 'tours/create/details',
  component: CreateTourDetailsPage,
  canActivate: [authGuard],
  canDeactivate: [pendingChangesGuard]
},
{
  path: 'tours/create/summary',
  component: CreateTourSummaryPage,
  canActivate: [authGuard],
  canDeactivate: [pendingChangesGuard]
}
```

Damit ist von Anfang an klar:

- nur eingeloggte User duerfen hinein
- beim Verlassen greift ein Leave-Guard

## Schritt 1: Die Details-Seite wird geoeffnet

Der User landet auf `CreateTourDetailsPage` in [create-tour-details-page.ts](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/features/tours/pages/create-tour/create-tour-details-page.ts).

Beim Laden passieren zwei wichtige Dinge:

1. Die Seite scrollt nach oben.
2. Das Formular wird mit dem aktuellen Draft vorbelegt.

```ts
constructor() {
  const draft = this.draftService.draft();

  this.detailsForm = this.formBuilder.nonNullable.group({
    name: [draft.name, [Validators.required]],
    transportType: [draft.transportType, [Validators.required]],
    from: [draft.from, [Validators.required]],
    to: [draft.to, [Validators.required]],
    description: [
      draft.description,
      [Validators.required, Validators.maxLength(this.maxDescriptionLength)]
    ]
  });

  afterNextRender(() => {
    window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
  });
}
```

### Was hier fachlich passiert

Wenn der User zum ersten Mal in den Flow kommt, ist der Draft leer. Dann startet das Formular leer.

Wenn der User spaeter auf `Summary` war und wieder auf `Edit` klickt, liest die Seite denselben Draft erneut ein. Dadurch sieht der User wieder dieselben Werte im Formular.

## Schritt 2: Warum hier `Reactive Forms` verwendet werden

Die Details-Seite ist die eigentliche Eingabeseite. Deshalb liegt die Formlogik in einem `FormGroup`.

Das passt gut fuer:

- Pflichtfelder
- `maxlength`
- spaetere Validierungsfehler
- sauberen Submit

Im Template [create-tour-details-page.html](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/features/tours/pages/create-tour/create-tour-details-page.html) sieht man die Bindung direkt:

```html
<form [formGroup]="detailsForm" class="create-tour-form">
  <label class="create-tour-field">
    <span>Tour Name</span>
    <input type="text" formControlName="name" />
  </label>

  <label class="create-tour-field create-tour-field--select">
    <span>Transport Type</span>
    <select formControlName="transportType">
      <option value=""></option>
      <option value="car">Car</option>
      <option value="bike">Bike</option>
      <option value="walk">Walk</option>
    </select>
  </label>
</form>
```

Der User tippt also nicht in lose Variablen der Komponente, sondern direkt in ein Angular-Formmodell.

## Schritt 3: Wo kommen die Daten zwischen `Details` und `Summary` hin?

Hier kommt der signal-basierte Draft-Service ins Spiel.

Der gemeinsame Zwischenstand liegt in [create-tour-draft.service.ts](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/features/tours/services/create-tour-draft.service.ts):

```ts
export interface CreateTourDraft {
  name: string;
  transportType: string;
  from: string;
  to: string;
  description: string;
}

const INITIAL_DRAFT: CreateTourDraft = {
  name: '',
  transportType: '',
  from: '',
  to: '',
  description: ''
};

@Injectable({ providedIn: 'root' })
export class CreateTourDraftService {
  readonly draft = signal<CreateTourDraft>(INITIAL_DRAFT);

  update(draft: CreateTourDraft): void {
    this.draft.set(draft);
  }

  reset(): void {
    this.draft.set(INITIAL_DRAFT);
  }
}
```

### Warum `signal` hier sinnvoll ist

Der Draft ist kein klassisches Formular mehr, sondern der gemeinsam genutzte Zwischenstand des Wizards.

Das Signal ist hier praktisch, weil:

- `Details` Werte hineinschreiben kann
- `Summary` Werte daraus lesen kann
- die Ruecknavigation `Edit` wieder auf denselben Stand zugreift
- bei bestaetigtem Leave alles zentral geloescht werden kann

Kurz:

- Eingabe = `Reactive Forms`
- seitenuebergreifender Wizard-Stand = `signal`

## Schritt 4: Der User fuellt das Formular aus

Angenommen, der User gibt Folgendes ein:

- `name = Vienna City Center Tour`
- `transportType = Car`
- `from = Stephansdom, 1010 Vienna`
- `to = Schloss Schoenbrunn, 1130 Vienna`
- `description = ...`

Diese Werte liegen zunaechst nur im `Reactive Form`.

Der Description-Counter liest direkt aus dem Formular:

```ts
protected get descriptionLength(): number {
  return this.detailsForm.controls.description.value.length;
}

protected get isDescriptionTooLong(): boolean {
  return this.descriptionLength > this.maxDescriptionLength;
}
```

Im Template:

```html
<textarea
  [class.create-tour-field__control--invalid]="isDescriptionTooLong"
  formControlName="description"
></textarea>
<small [class.create-tour-field__counter--invalid]="isDescriptionTooLong">
  {{ descriptionLength }}/{{ maxDescriptionLength }}
</small>
```

Das heisst:

- der Counter zeigt live den aktuellen Stand
- bei Ueberschreitung wird das Feld rot markiert

## Schritt 5: Der User klickt auf `Go to summary`

Jetzt kommt der entscheidende Uebergang vom Formular in den Draft.

Die Methode `goToSummary()` in [create-tour-details-page.ts](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/features/tours/pages/create-tour/create-tour-details-page.ts) sieht so aus:

```ts
protected goToSummary(): void {
  this.detailsForm.markAllAsTouched();

  if (this.detailsForm.invalid) {
    return;
  }

  this.draftService.update(this.detailsForm.getRawValue());
  this.allowRouteChange = true;
  void this.router.navigateByUrl('/tours/create/summary');
}
```

### Was in dieser Reihenfolge passiert

1. Alle Felder werden mit `markAllAsTouched()` als geprueft markiert.
2. Wenn etwas invalid ist, bleibt der User auf `Details`.
3. Wenn alles gueltig ist, werden die aktuellen Formularwerte in das `signal` geschrieben:

```ts
this.draftService.update(this.detailsForm.getRawValue());
```

4. Danach wird mit `allowRouteChange = true` festgehalten, dass diese Navigation ein interner Wizard-Wechsel ist.
5. Erst dann wird nach `/tours/create/summary` navigiert.

## Schritt 6: Warum der Leave-Guard hier nicht stoeren darf

Beide Wizard-Seiten haben `canDeactivate`.

Normalerweise bedeutet das:

- wenn der User die Seite verlaesst
- dann soll ein Popup erscheinen

Beim internen Wechsel `Details -> Summary` waere das aber falsch. Deshalb gibt es `allowRouteChange`.

Die zentrale Methode ist:

```ts
canDeactivate(): boolean | Promise<boolean> {
  if (this.allowRouteChange) {
    this.allowRouteChange = false;
    return true;
  }

  if (this.pendingLeaveResolver) {
    return false;
  }

  this.showLeaveDialog = true;

  return new Promise<boolean>((resolve) => {
    this.pendingLeaveResolver = resolve;
  });
}
```

Das bedeutet:

- interner Wizard-Wechsel mit `allowRouteChange = true` => sofort erlaubt
- jede andere Navigation => Dialog und Guard-Pause

## Schritt 7: Die Summary-Seite liest die Daten

Auf `/tours/create/summary` landet der User in [create-tour-summary-page.ts](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/features/tours/pages/create-tour-summary/create-tour-summary-page.ts).

Auch diese Seite liest den Draft ueber den Service:

```ts
protected readonly draftService = inject(CreateTourDraftService);

protected get draft() {
  return this.draftService.draft();
}
```

Im Template [create-tour-summary-page.html](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/features/tours/pages/create-tour-summary/create-tour-summary-page.html) werden diese Werte read-only angezeigt:

```html
<input type="text" [value]="draft.name" readonly />
<input type="text" [value]="draft.transportType" readonly />
<input type="text" [value]="draft.from" readonly />
<input type="text" [value]="draft.to" readonly />
<textarea readonly>{{ draft.description }}</textarea>
```

Der User sieht also genau die Werte, die vorher in `Details` eingegeben und beim Schrittwechsel in den Draft geschrieben wurden.

## Schritt 8: Der User klickt auf `Edit`

Wenn der User merkt, dass `From` oder `Description` noch angepasst werden soll, klickt er auf `Edit`.

Die Methode auf der Summary-Seite:

```ts
protected editTour(): void {
  this.allowRouteChange = true;
  void this.router.navigateByUrl('/tours/create/details');
}
```

Wieder passiert Folgendes:

1. Die Navigation wird als interner Wizard-Wechsel markiert.
2. Der Leave-Guard blockiert nicht.
3. Die Details-Seite baut ihr Formular erneut aus dem Draft auf.
4. Der User sieht die zuvor eingegebenen Werte wieder im Formular.

## Schritt 9: Was passiert, wenn der User den Flow ueber die Sidebar verlaesst?

Nehmen wir an, der User ist auf `Summary` und klickt in der Sidebar auf `Dashboard`.

Jetzt ist es keine interne Wizard-Navigation mehr. Also greift der Guard.

Der Guard selbst liegt in [pending-changes.guard.ts](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/core/guards/pending-changes.guard.ts):

```ts
export interface PendingChangesAware {
  canDeactivate: () => boolean | Promise<boolean>;
}

export const pendingChangesGuard: CanDeactivateFn<PendingChangesAware> = (
  component
) => component.canDeactivate();
```

Der Guard entscheidet nicht selbst, sondern ruft `component.canDeactivate()` auf.

In der Seite passiert dann:

```ts
this.showLeaveDialog = true;

return new Promise<boolean>((resolve) => {
  this.pendingLeaveResolver = resolve;
});
```

### Was das konkret bedeutet

1. Angular will von `Summary` weg navigieren.
2. Angular fragt `canDeactivate()`.
3. Die Seite oeffnet den Dialog.
4. Gleichzeitig gibt sie ein `Promise<boolean>` zurueck.
5. Angular wartet auf dieses Promise.

Die Navigation ist also gestartet, aber pausiert.

## Schritt 10: Wie der Dialog mit der Seite spricht

Der Dialog ist eine Shared Component:

- [confirm-dialog.ts](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/shared/components/confirm-dialog/confirm-dialog.ts)
- [confirm-dialog.html](/mnt/c/Users/wenha/Documents/FHTW/4.%20Semester/SWEN2/Tour/tour_planner/frontend/src/app/shared/components/confirm-dialog/confirm-dialog.html)

Die Outputs:

```ts
@Output() confirm = new EventEmitter<void>();
@Output() cancel = new EventEmitter<void>();
```

Die Seite bindet diese Events so:

```html
<app-confirm-dialog
  [visible]="showLeaveDialog"
  (confirm)="confirmLeave()"
  (cancel)="stayOnPage()"
/>
```

Also:

- `Leave page` im Dialog => `confirmLeave()`
- `Stay here` im Dialog => `stayOnPage()`

## Schritt 11: User bestaetigt das Verlassen

Wenn der User wirklich abbrechen will, wird `confirmLeave()` aufgerufen.

Beispiel aus den Wizard-Seiten:

```ts
protected confirmLeave(): void {
  this.draftService.reset();
  this.showLeaveDialog = false;
  this.pendingLeaveResolver?.(true);
  this.pendingLeaveResolver = null;
}
```

### Reihenfolge bei bestaetigtem Leave

1. `draftService.reset()` leert das `signal`
2. der Dialog wird geschlossen
3. das wartende Promise wird mit `true` aufgeloest
4. Angular setzt die bereits angefragte Navigation fort

Fachlich heisst das:

- der User hat den Wizard wirklich verlassen
- der Zwischenstand soll verworfen werden
- ein spaeter neuer `Create Tour`-Durchlauf startet leer

## Schritt 12: User bleibt doch auf der Seite

Wenn der User im Dialog `Stay here` klickt, laeuft `stayOnPage()`:

```ts
protected stayOnPage(): void {
  this.showLeaveDialog = false;
  this.pendingLeaveResolver?.(false);
  this.pendingLeaveResolver = null;
}
```

Dann passiert:

1. Dialog schliesst
2. Promise wird mit `false` aufgeloest
3. Angular bricht die Navigation ab
4. die aktuelle Seite bleibt offen
5. der Draft im `signal` bleibt erhalten

Das ist wichtig, weil die bisher eingegebenen Daten dann nicht verloren gehen.

## Gesamtbild in einem Satz

Der aktuelle `Create Tour`-Flow nutzt:

- `Reactive Forms` fuer die echte Eingabe auf `Details`
- ein `signal` als gemeinsamen Draft zwischen `Details` und `Summary`
- einen Leave-Guard mit bestaetigendem Dialog fuer externes Verlassen des Wizards
- `allowRouteChange`, damit interne Wizard-Navigation ohne Popup funktioniert

## Technische Kerngedanken

### Warum nicht alles nur mit Signals?

Weil die Eingabeseite mit `Reactive Forms` sauberer ist fuer:

- Validierung
- Formularzustand
- Pflichtfelder
- spaeteren Submit

### Warum nicht alles nur im FormGroup?

Weil `Summary` eine eigene Seite ist. Die Form aus `Details` lebt nicht automatisch seitenuebergreifend weiter. Deshalb braucht der Flow einen gemeinsamen Zustand.

### Warum ein signal-Service?

Weil der Draft damit:

- zentral
- einfach lesbar
- leicht zuruecksetzbar
- sauber zwischen mehreren Seiten teilbar

ist.

## Naechste sinnvolle Schritte

Der aktuelle Flow ist fuer den Wizard vorbereitet, aber noch nicht final.

Noch offen:

- Feldfehlermeldungen im Details-Template
- echte Weitergabe an das Backend bei `Save`
- finaler `Done`-Schritt
- Leaflet-Integration
- echte Distanz- und Zeitwerte
