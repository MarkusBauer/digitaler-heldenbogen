<#macro eigenschaften>
    <div class="block kampf-eigenschaften-block">
        <div class="eigenschaft" data-title="Lebensenergie"><span class="eigenschaft-name">LeP</span> ${held.getEigenschaft("Lebensenergie")}</div>
        <div class="eigenschaft" data-title="Ausdauer"><span class="eigenschaft-name">AuP</span> ${held.getEigenschaft("Ausdauer")}</div>
        <#if renderer.showEigenschaft("Astralenergie")>
            <div class="eigenschaft" data-title="Astralenergie">
                <span class="eigenschaft-name">AsP</span>
                ${held.getEigenschaft("Astralenergie")}
                <#if !renderer.showEigenschaft("Karmaenergie")>
                    <small data-title="Astralenergie-Regeneration (pro Ruhephase)">+${held.aspregeneration}</small>
                </#if>
            </div>
        </#if>
        <#if renderer.showEigenschaft("Karmaenergie")>
            <div class="eigenschaft" data-title="Karmaenergie"><span class="eigenschaft-name">KaP</span> ${held.getEigenschaft("Karmaenergie")}</div>
        </#if>

        <div class="eigenschaft" data-title="Initiative">
            <span class="eigenschaft-name">INI</span>
            ${held.kampfset.ini}
        </div>
        <div class="eigenschaft">
            <span class="eigenschaft-name" data-title="Ausweichen">AW</span>
            ${held.getEigenschaft("Ausweichen")}
        </div>
        <div class="eigenschaft">
            <span class="eigenschaft-name" data-title="Wundschwelle">WS</span>
            ${held.wundschwelle}
        </div>
    </div>
</#macro>

<#macro waffen>
    <div class="block kampf-block">
        <#list held.kampfset.nahkampfWaffen as waffe>
            <div class="nahkampfwaffe">
                <span class="waffe-name">
                    <#if talentIcon[waffe.waffentalent]?has_content><span class="icon-${talentIcon[waffe.waffentalent]}" data-title="Waffentalent: ${waffe.waffentalent}"></span></#if>
                    ${renderer.waffenName1(waffe.name)}
                    <#if renderer.waffenName2(waffe.name)?has_content>
                        <small>${renderer.waffenName2(waffe.name)}</small>
                    </#if>
                    <#if waffe.bh><small class="icon-melee" data-title="Beidhändiger Kampf"></small></#if>
                    <#if waffe.schildnummer &gt; -1><small class="icon-${talentIcon[held.kampfset.schilder[waffe.schildnummer].typ]}" data-title="${held.kampfset.schilder[waffe.schildnummer].typ} ${waffe.schildnummer +1}"></small></#if>
                </span>
                <span class="waffe-wert"><span class="bez" data-title="Attacke">AT</span><#if waffe.at &gt; 0>${waffe.at}</#if></span>
                <span class="waffe-wert"><span class="bez" data-title="Parade">PA</span><#if waffe.pa &gt; 0>${waffe.pa}</#if></span>
                <span class="waffe-wert"><span class="bez" data-title="Trefferpunkte">TP</span>${renderer.waffeTP(waffe.tpinkl)}</span>
                <span class="waffe-wert"><span class="bez" data-title="Distanzklasse">DK</span>${waffe.dk}</span>
                <span class="waffe-wert"><span class="bez" data-title="Initiative">INI</span>${waffe.ini}</span>
                <span class="waffe-wert"><span class="bez" data-title="Bruchfaktor">BF</span><#if waffe.bfakt &gt; 0>${waffe.bfakt}<#elseif waffe.at &gt; 0>-</#if></span>
            </div>
        </#list>

        <#list held.kampfset.schilder as schild>
            <div class="schild">
                <span class="waffe-name">
                    <#if talentIcon[schild.typ]?has_content><span class="icon-${talentIcon[schild.typ]}" data-title="${schild.typ}"></span></#if>
                    ${renderer.waffenName1(schild.name)}
                    <#if renderer.waffenName2(schild.name)?has_content>
                        <small>${renderer.waffenName2(schild.name)}</small>
                    </#if>
                </span>
                <span class="waffe-wert"><span class="bez" data-title="Parade">>PA</span>${schild.pa}</span>
                <span class="waffe-wert"><span class="bez" data-title="Waffen-Malus">>WM</span>${schild.mod}</span>
                <span class="waffe-wert"><span class="bez" data-title="Bruchfaktor">BF</span>${schild.bfakt}</span>
            </div>
        </#list>

        <div class="waffenlos">
            <#if held.kampfset.raufen.at != '0'>
                <span class="waffe-name"><span class="icon-${talentIcon["Raufen"]}"></span> Raufen</span>
                <span class="waffe-wert"><span class="bez" data-title="Attacke">AT</span>${held.kampfset.raufen.at}</span>
                <span class="waffe-wert"><span class="bez" data-title="Parade">PA</span>${held.kampfset.raufen.pa}</span>
                <span class="waffe-wert"><span class="bez" data-title="Trefferpunkte">TP</span>${renderer.waffeTP(held.kampfset.raufen.tp)}</span>
            </#if>
            <#if held.kampfset.ringen.at != '0'>
                <span class="waffe-name"><span class="icon-${talentIcon["Raufen"]}"></span> Ringen</span>
                <span class="waffe-wert"><span class="bez" data-title="Attacke">AT</span>${held.kampfset.ringen.at}</span>
                <span class="waffe-wert"><span class="bez" data-title="Parade">PA</span>${held.kampfset.ringen.pa}</span>
                <span class="waffe-wert"><span class="bez" data-title="Trefferpunkte">TP</span>${renderer.waffeTP(held.kampfset.ringen.tp)}</span>
            </#if>
        </div>

        <#list held.kampfset.fernkampfWaffen as waffe>
            <div class="fernkampfwaffe">
                <span class="waffe-name <#if waffe.ladezeit &gt; 0>with-ladezeit</#if>">
                    <#if talentIcon[waffe.kampftalent]?has_content><span class="icon-${talentIcon[waffe.kampftalent]}" data-title="Waffentalent: ${waffe.kampftalent}"></span></#if>
                    ${renderer.waffenName1(waffe.name)}
                    <#if renderer.waffenName2(waffe.name)?has_content>
                        <small>${renderer.waffenName2(waffe.name)}</small>
                    </#if>
                </span>
                <#if waffe.ladezeit &gt; 0>
                    <span class="bez lz" data-title="Ladezeit (in Aktionen)">
                        LZ
                        <span class="waffe-wert lz">${waffe.ladezeit}</span>
                    </span>
                </#if>

                <span class="bez" data-title="Fernkampf-Attacke">FK</span>
                <span class="waffe-wert fk">${waffe.at}</span>
                <span class="bez" data-title="Entfernungs-Kategorien">Entf</span>
                <#list renderer.split(waffe.reichweite) as s>
                    <span class="waffe-wert reichweite list">${s}</span>
                </#list>

                <span class="bez" data-title="Trefferpunkte">TP</span>
                <span class="waffe-wert tp">${renderer.waffeTP(waffe.tp)}</span>
                <span class="bez" data-title="Trefferpunkte pro Entfernungs-Kategorie">TP+</span>
                <#list renderer.split(waffe.tpmod) as s>
                    <span class="waffe-wert tpmod list">${s}</span>
                </#list>
            </div>
        </#list>

        <div class="ruestung">
            <span class="waffe-name"><span class="icon-${talentIcon["Rüstung"]}"></span> Rüstung</span>
            <#if held.kampfset.ruestungIsZero()>
                <#list ["gesamtschutz", "behinderung"] as k >
                    <span class="waffe-wert ruestung-${short.wert(k)}" data-title="${tooltips[k]}"><span class="bez">${short.wert(k)}</span>${held.kampfset.ruestungsZonen[k]}</span>
                </#list>
                <#list ["kopf", "brust", "ruecken", "bauch", "linkerarm", "linkesbein", "rechterarm", "rechtesbein"] as k >
                    <span class="waffe-wert ruestung-${short.wert(k)}" data-title="${tooltips[k]}"><span class="bez">${short.wert(k)}</span></span>
                </#list>
            <#else>
                <#list ["gesamtschutz", "behinderung", "kopf", "brust", "ruecken", "bauch", "linkerarm", "linkesbein", "rechterarm", "rechtesbein"] as k >
                    <span class="waffe-wert ruestung-${short.wert(k)}" data-title="${tooltips[k]}"><span class="bez">${short.wert(k)}</span>${held.kampfset.ruestungsZonen[k]}</span>
                </#list>
            </#if>
        </div>
    </div>
</#macro>