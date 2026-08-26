<#macro html>
    <!doctype html>
    <html lang="de">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${held.angaben.name}</title>
        ${resources.css?no_esc}
    </head>
    <body>
    <div class="buttons">
        <button class="btn-wide hidden-on-small-screen" type="button" data-theme-toggle aria-label="Seiten / Seitenloses Layout" title="Seiten / Seitenloses Layout">&harr;</button>
        <button class="btn-light" type="button" data-theme-toggle aria-label="Helles Design" title="Helles Design">&#x263C;</button>
        <button class="btn-dark" type="button" data-theme-toggle aria-label="Dunkles Design" title="Dunkles Design">&#x263E;</button>
    </div>

    <#nested>

    <footer class="attribution no-print">
        <span>
        <a href="https://github.com/intrinsical/tw-dnd/tree/main/icons" target="_blank" rel="nofollow noreferrer">Intrinsical's D&D 5e Icon Set</a>
        by <a href="https://github.com/intrinsical" target="_blank" rel="nofollow noreferrer">David Kor Kian Wei</a>
        is licensed under <a href="https://creativecommons.org/licenses/by-sa/4.0/?ref=chooser-v1" target="_blank" rel="nofollow noreferrer">Creative Commons Attribution-ShareAlike 4.0 International</a>
        </span>
        <span>rapier sword by Kiseki from <a href="https://thenounproject.com/browse/icons/term/rapier-sword/" target="_blank" rel="nofollow noreferrer">Noun Project</a> (CC BY 3.0)</span>
        <span>dagger by iconcheese from <a href="https://thenounproject.com/browse/icons/term/dagger/" target="_blank"  rel="nofollow noreferrer">Noun Project</a> (CC BY 3.0)</span>
        <span>knife throwing by Lars Meiertoberens from <a href="https://thenounproject.com/browse/icons/term/knife-throwing/" target="_blank" rel="nofollow noreferrer">Noun Project</a> (CC BY 3.0)</span>
        <span>axe throwing by Lars Meiertoberens from <a href="https://thenounproject.com/browse/icons/term/axe-throwing/" target="_blank" rel="nofollow noreferrer">Noun Project</a> (CC BY 3.0)</span>
        <span>rapier by Idad Muchlisin Ro Is from <a href="https://thenounproject.com/browse/icons/term/rapier/" target="_blank" rel="nofollow noreferrer">Noun Project</a> (CC BY 3.0)</span>
        <span>Chain by Alum Design from <a href="https://thenounproject.com/browse/icons/term/chain/" target="_blank" rel="nofollow noreferrer">Noun Project</a> (CC BY 3.0)</span>
    </footer>

    ${resources.js?no_esc}
    </body>
    </html>
</#macro>