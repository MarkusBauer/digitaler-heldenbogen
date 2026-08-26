<#macro profile>
    <div class="block header-block <#if portrait??>with-portrait<#else>without-portrait</#if>">
        <#if portrait??>
            <div class="portrait-container" data-w="${portrait.width?c}" data-h="${portrait.height?c}">
                <#if portrait.mime == 'jpg'>
                    <div class="portrait portrait-img portrait-img-bg"></div>
                </#if>
                <a alt="Charakterbild" class="portrait portrait-img" data-toggle="modal-open"></a>
                <div class="modal portrait-img"><a class="modal-close-icon" data-toggle="modal-close">&times;</a></div>
            </div>
        </#if>
        <div class="header-container">
            <h1>${held.angaben.name}</h1>
            <span class="profession" <#if profession != held.angaben.profession.text>data-title="${held.angaben.profession.text}"</#if>>${profession}</span>
        </div>
        <div class="angaben-container">
            <span class="angabe" <#if rasse != held.angaben.rasse>data-title="${held.angaben.rasse}"</#if>><span class="angabe-name">Rasse</span>${rasse}</span>
            <span class="angabe" <#if kultur != held.angaben.kultur>data-title="${held.angaben.kultur}"</#if>><span class="angabe-name">Kultur</span>${kultur}</span>
            <#--<span class="angabe"><span class="angabe-name">Geschlecht</span>${held.angaben.geschlecht}</span>-->

            <span class="angabe"><span class="angabe-name">Größe</span><span>${held.angaben.groesse}<span class="unit">cm</span></span></span>
            <span class="angabe"><span class="angabe-name">Gewicht</span><span>${held.angaben.gewicht}<span class="unit">Stein</span></span></span>
            <span class="angabe"><span class="angabe-name">Geburtstag</span>${held.angaben.geburtstag}</span>
            <span class="angabe"><span class="angabe-name">Alter</span><span>${held.angaben.alter}<span class="unit">Jahre</span></span></span>
            <span class="angabe"><span class="angabe-name">Haarfarbe</span>${held.angaben.haarfarbe}</span>
            <span class="angabe"><span class="angabe-name">Augenfarbe</span>${held.angaben.augenfarbe}</span>
            <#if held.angaben.stand?has_content>
                <span class="angabe"><span class="angabe-name">Stand</span>${held.angaben.stand}</span>
            </#if>
            <#if held.angaben.titel?has_content>
                <span class="angabe"><span class="angabe-name">Titel</span>${held.angaben.titel}</span>
            </#if>
            <#if held.angaben.gilde?has_content>
                <span class="angabe"><span class="angabe-name">Gilde</span>${held.angaben.gilde}</span>
            </#if>

            <#if held.gp.start != 110 && held.gp.start != 0>
                <span class="angabe"><span class="angabe-name">GP</span><span>${held.gp.start}</span></span>
            </#if>
            <span class="angabe"><span class="angabe-name">AP</span><span data-title="Freie Abenteuerpunkte: ${held.ap.frei}">${held.ap.gesamt}</span></span>
        </div>
        <div class="hintergrund-container">
            <#if renderer.arrayHasContent(held.angaben.aussehen)>
                <span class="angabe-long"><span class="angabe-name">Aussehen</span>${renderer.join(held.angaben.aussehen)}</span>
            </#if>
            <#if renderer.arrayHasContent(held.angaben.familie)>
                <span class="angabe-long"><span class="angabe-name">Familie</span>${renderer.join(held.angaben.familie)}</span>
            </#if>
        </div>
    </div>


    <#if portrait??>
        <style>.portrait-img {background-image: url("data:${portrait.mime};base64,${portrait.base64()}");}</style>
    </#if>
</#macro>