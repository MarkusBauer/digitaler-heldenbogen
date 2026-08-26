<#macro talente_list talente=talente empty=2>
    <div class="block talent-block">
        <#list talente as talentGruppe>
            <#if talentGruppe.isVisible()>
                <div class="talent-gruppe talent-gruppe-${talentGruppe.getIdent()}">
                    <#if talentGruppe.name?has_content>
                        <h3>${talentGruppe.name}</h3>
                        <#if talentGruppe.hasAtPa()>
                            <strong class="talent-at">AT</strong>
                            <strong class="talent-pa">PA</strong>
                        </#if>
                        <#if talentGruppe.getProbe()?has_content>
                            <strong class="talent-probe">${talentGruppe.getProbe()}</strong>
                        </#if>
                        <#if talentGruppe.hasK()>
                            <strong class="talent-k">K</strong>
                        </#if>
                        <strong class="talent-wert">TaW</strong>
                    </#if>
                    <#list talentGruppe.talente as t>
                        <span class="talent-name ${t.getClasses()}" <#if t.getName() != t.getTooltip()>data-title="${t.getTooltip()}"</#if>>
                            <#if t.getName() != t.getShortName()><span data-toggle="abbrev" data-text="${t.getShortName()}"></#if>
                                ${t.getName()}
                            <#if t.getName() != t.getShortName()></span></#if>
                            <#if t.mirakelplus><span class="mirakelplusminus">+</span></#if>
                            <#if t.mirakelminus><span class="mirakelplusminus">-</span></#if>
                            <#if t.meisterhandwerk><span class="meisterhandwerk"></span></#if>
                        </span>
                        <#if t.getProbe()?has_content>
                            <span class="talent-probe ${t.getClasses()}">${t.getProbe()}</span>
                        </#if>
                        <#if t.getBehinderung()??>
                            <span class="talent-be ${t.getClasses()}">${t.getBehinderung()}</span>
                        </#if>
                        <#if t.at &gt; 0>
                            <span class="talent-at ${t.getClasses()}">${t.at}</span>
                            <span class="talent-pa ${t.getClasses()}"><#if t.pa &gt; 0>${t.pa}</#if></span>
                        </#if>
                        <#if t.sprachkomplexitaet?has_content>
                            <span class="talent-k ${t.getClasses()}">${t.sprachkomplexitaet}</span>
                        </#if>
                        <span class="talent-wert ${t.getClasses()}">${t.wert}</span>
                        <#if t.ts?has_content>
                            <span class="talent-ts ${t.getClasses()}">${t.getTS()}</span>
                        </#if>
                    </#list>
                </div>
            </#if>
        </#list>
        <#if empty != 0>
            <div class="talent-gruppe talent-gruppe-empty">
                <#list 0..<empty as i>
                    <div class="talent-empty"></div>
                </#list>
            </div>
        </#if>
    </div>
</#macro>
