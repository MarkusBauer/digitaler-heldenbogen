<#import "base.html.ftlh" as base>
<#import "blocks/basic.html.ftlh" as basic>
<#import "blocks/kampf.html.ftlh" as kampf>
<#import "blocks/profile.html.ftlh" as profile>
<#import "blocks/talentlist.html.ftlh" as talentlist>
<#import "blocks/wesen.html.ftlh" as wesenblocks>
<#import "blocks/zauber.html.ftlh" as zauberblocks>
<@base.html>

    <div class="page page-kompaktbogen">
        <div class="sticky-bar">
            <@basic.top_eigenschaften eigenschaften=["Mut","Klugheit","Intuition","Charisma","Fingerfertigkeit","Gewandtheit","Konstitution","Körperkraft","Sozialstatus","Magieresistenz","Geschwindigkeit"]/>
        </div>
        <@profile.profile/>
        <@basic.vornachteile/>

        <div class="row">
            <@basic.sonderfertigkeiten_list class="col" filter=renderer.sfVisibleFirstPage/>

            <div class="col">
                <@kampf.eigenschaften/>
                <@kampf.waffen/>
            </div>
        </div>

        <@talentlist.talente_list/>

        <@basic.notizen/>

        <@basic.footer/>
    </div>

    <#if zauber?size != 0 || rituale?size != 0>
        <div class="page page-zauberbogen page-split-if-necessary">
            <div class="sticky-bar">
                <@basic.top_eigenschaften eigenschaften=["Mut","Klugheit","Intuition","Charisma","Fingerfertigkeit","Gewandtheit","Konstitution","Körperkraft","Magieresistenz","Astralenergie","Astralenergie-Regeneration","Karmaenergie"]/>
            </div>

            <@basic.vornachteile filter=renderer.isVorteilMagisch />
            <#if renderer.hasAnySfGroupMagisch(sonderfertigkeiten)>
                <@basic.sonderfertigkeiten_list filterGroup=renderer.isSfGroupMagisch filter=renderer.sfVisibleRitualPage headers=false />
            </#if>

            <#if zauber?size != 0>
                <@zauberblocks.zauberblock/>
            </#if>

            <#if rituale?size != 0>
                <#if zauber?size != 0>
                    <#if ritualkenntnis.talente?size == 1><div class="grid-2"></#if>
                    <h2>Rituale <#if held.getEigenschaft("Karmaenergie") &gt; 0> / Liturgien</#if></h2>
                </#if>
                <#if ritualkenntnis.talente?size != 0>
                    <@talentlist.talente_list talente=[ritualkenntnis] empty=0/>
                </#if>
                <#if zauber?size != 0 && ritualkenntnis.talente?size == 1></div></#if>

                <@zauberblocks.ritualblock/>
            </#if>

            <@basic.footer/>
        </div>
    </#if>

    <#if wesen?size != 0>
        <div class="page page-wesen">
            <#list wesen as w>
                <@wesenblocks.block_wesen w/>
            </#list>
            <@basic.footer/>
        </div>
    </#if>

</@base.html>