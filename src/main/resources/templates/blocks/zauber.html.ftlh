<#macro zauberblock>
    <div class="block block-zauber">
        <div class="zauber-liste split-liste">
            <#list zauber as z>
                <span class="zauber-name <#if z.hauszauber>hauszauber</#if>" data-title="${z.getTooltip()}">
                    ${z.displayName}
                    <#if z.repraesentation != defaultRepraesentation>
                        <span class="repr" data-title="Repräsentation ${z.repraesentation}">${short.repraesentation(z.repraesentation)}</span>
                    </#if>
                </span>
                <span class="zauber-probe">${z.getProbe()}</span>
                <span class="zauber-wert">${z.wert}</span>
                <span class="zauber-infos">
                    <#list ["spezialisierungen", "zauberdauer", "kosten", "reichweite", "wirkungsdauer", "kontrollwert", "anmerkung"] as what>
                        <#if z[what]?has_content>
                            <span class="zauber-info" data-title="${tooltips[what]}"><span class="zauber-info-header">${short.wert(what)}</span>${z[what]}</span>
                        </#if>
                    </#list>
                </span>
            </#list>
        </div>
    </div>
</#macro>


<#macro ritualblock>
    <div class="block block-rituale">
        <div class="ritual-liste split-liste">
            <#list rituale as r>
                <span class="zauber-name">${r.name}</span>
                <#if r.probe?has_content>
                    <#if r.probe?length <= 30>
                        <span class="zauber-probe">${r.probe}</span>
                    <#else>
                        <span class="zauber-probe in-new-line">${r.probe}</span>
                    </#if>
                </#if>
                <span class="zauber-infos">
                    <#if r.custom?has_content>
                        <span class="zauber-info">${r.custom}</span>
                    </#if>
                    <#list ["dauer", "kosten", "wirkung", "kommentar"] as what>
                        <#if r[what]?has_content>
                            <#if what == "kosten" && r.istBereich("Liturgie")>
                                <span class="zauber-info" data-title="Karmapunkte-Kosten"><span class="zauber-info-header">KaP</span>${r[what]}</span>
                            <#else>
                                <span class="zauber-info" data-title="${tooltips[what]}"><span class="zauber-info-header">${short.eigenschaft(what)}</span>${r[what]}</span>
                            </#if>
                        </#if>
                    </#list>
                </span>
            </#list>
        </div>
    </div>
</#macro>