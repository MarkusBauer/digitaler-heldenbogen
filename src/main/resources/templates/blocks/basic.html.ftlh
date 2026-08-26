<#function any lst filter>
    <#list lst as item>
        <#if filter(item)><#return true/></#if>
    </#list>
    <#return false/>
</#function>

<#macro footer>
    <footer>
        ${held.angaben.name}, ${held.ap.gesamt}&nbsp;AP, ${date}
    </footer>
</#macro>

<#macro top_eigenschaften eigenschaften>
    <div class="block top-eigenschaften-block">
        <#list eigenschaften as eigenschaft>
            <#if renderer.showEigenschaft(eigenschaft)>
                <div class="eigenschaft" data-title="${eigenschaft}">
                    <span class="eigenschaft-name">${short.eigenschaft(eigenschaft)}</span>
                    <#if eigenschaft == "Geschwindigkeit">
                        ${held.kampfset.geschwindigkeitinklbe}
                    <#elseif eigenschaft == "Astralenergie-Regeneration">
                        ${held.aspregeneration}
                    <#else>
                        ${held.getEigenschaft(eigenschaft)}
                    </#if>
                </div>
            </#if>
        </#list>
    </div>
</#macro>

<#macro vornachteile filter=renderer.filterTrue>
    <div class="block vorteile-block">
        <h2>Vorteile und Nachteile</h2>
        <#list vorteile as vorteil>
            <#if filter(vorteil)>
                <span class="vornachteil vorteil" <#if vorteil.getTooltip()?has_content>data-title="${vorteil.getTooltip()}"</#if>>${vorteil.displayName};</span>
            </#if>
        </#list>
        <#list nachteile as nachteil>
            <#if filter(nachteil)>
                <span class="vornachteil nachteil" <#if nachteil.getTooltip()?has_content>data-title="${nachteil.getTooltip()}"</#if>>${nachteil.displayName};</span>
            </#if>
        </#list>
    </div>
</#macro>

<#macro sonderfertigkeiten_list class="" filter=renderer.filterTrue filterGroup=renderer.filterTrue headers=true>
    <div class="block sonderfertigkeiten-block ${class}">
        <h2>Sonderfertigkeiten</h2>
        <#list sonderfertigkeiten as gruppe>
            <#if gruppe.sfs?has_content && filterGroup(gruppe) && any(gruppe.sfs, filter)>
                <span class="sf-gruppe">
                    <#if gruppe.name?has_content && headers><span class="sf-header">${gruppe.name}:</span></#if>
                    <#list gruppe.sfs as sf>
                        <#if filter(sf) >
                            <span class="sf" <#if sf.kommentar?has_content>data-title="${sf.kommentar}"</#if>>${sf.name};</span>
                        </#if>
                    </#list>
                </span>
            </#if>
        </#list>
    </div>
</#macro>

<#macro notizen>
    <div class="block notizen-block">
        <h2>Notizen</h2>
    </div>
</#macro>