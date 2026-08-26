function last(arr) {
    return arr.item(arr.length - 1);
}

function hatVorteil(name) {
    for (let v of document.querySelectorAll('.vorteil')) {
        if (v.textContent.includes(name)) return true;
    }
    return false;
}

function remToPixels(rem) {
    return rem * parseFloat(getComputedStyle(document.documentElement).fontSize);
}

function improvePortraitLayout() {
    let container = document.querySelector('.portrait-container');
    if (!container) return;
    let w = parseInt(container.dataset.w);
    let h = parseInt(container.dataset.h);
    let containScale = Math.min(container.clientWidth / w, container.clientHeight / h);
    let coverScale = Math.max(container.clientWidth / w, container.clientHeight / h);
    let requiredFillScale = coverScale / containScale - 1;
    console.log('Required fill scale:', (requiredFillScale * 100).toFixed(2) + '%');

    if (requiredFillScale < 0.09) {
        container.classList.add('stretched');
    }
}

function improveKompaktbogenLayout() {
    let page = document.querySelector('.page-kompaktbogen');
    if (!page) return;
    let header = page.querySelector('.header-block');
    let notizen = page.querySelector('.notizen-block');
    let talente = page.querySelector('.talent-block');
    let sf = page.querySelector('.sonderfertigkeiten-block');
    let kampf = page.querySelector('.kampf-block');

    function spaceRequired() {
        console.log('Required? =>', talente.scrollHeight - talente.clientHeight, header.scrollHeight - header.clientHeight, talente.clientHeight < talente.scrollHeight - 5 || header.clientHeight < header.scrollHeight - 5);
        return talente.clientHeight < talente.scrollHeight - 5 || header.clientHeight < header.scrollHeight - 5;
    }

    function removeEmptyNotizen() {
        notizen.classList.remove('hidden-on-page');
        if (notizen.clientHeight === 0 || spaceRequired()) {
            notizen.classList.add('hidden-on-page');
        }
    }

    // try to make space by removing the notes
    removeEmptyNotizen();

    // check if we can get sth out of SF list
    let rightColHeight = kampf.offsetTop + kampf.clientHeight;
    if (spaceRequired() && sf.scrollHeight > rightColHeight + 5) {
        sf.classList.add('compressed');
    }

    // Compress SF list (font size) if required (and helpful)
    if (spaceRequired() && sf.scrollHeight > rightColHeight + 10) {
        sf.classList.add('compressed-2');
    }

    // check if the header area consumes too much space
    if (spaceRequired()) {
        let profession = page.querySelector('.header-container .profession');
        let lines = profession.clientHeight / parseFloat(getComputedStyle(profession, null).lineHeight);
        if (lines > 2.5) {
            page.querySelector('.header-container').classList.add('long-profession');
        }
    }

    // resize talente to get more space
    applyAbbreviations(talente);
    if (spaceRequired()) {
        talente.classList.add('smaller1');
        applyAbbreviations(talente);
    }
    if (spaceRequired()) {
        talente.classList.add('smaller2');
        talente.classList.remove('smaller1');
        applyAbbreviations(talente);
        removeEmptyNotizen();
    }

    talente.querySelectorAll('.talent-gruppe').forEach(el => {
        if (el.scrollWidth > el.clientWidth + 1) {
            applyAllAbbreviations(el);
        }
    });

    // Add empty talent rows while there is space
    if (!page.classList.contains('wide')) {
        let lastTalent = talente.querySelector('.talent-empty:last-child');
        while (talente.clientHeight - lastTalent.offsetTop - 2 * lastTalent.offsetHeight > 5) {
            lastTalent = document.createElement('div');
            lastTalent.classList.add('talent-empty');
            talente.querySelector('.talent-gruppe-empty').appendChild(lastTalent);
        }
    }

    console.log('Final height of header:', header.clientHeight, header.scrollHeight);
    improvePortraitLayout();
}


// === Utils to split pages ===
var loopLimit = 32;

function splitPage(page, firstChildOnNewPage) {
    if (loopLimit-- < 0) return;
    console.time('Splitting page ' + page);
    // create a new, empty page
    let newPage = cloneEmpty(page);

    // copy over some items (header, footer)
    for (let child of page.children) {
        if (child.classList.contains('sticky-bar')) {
            let clone = child.cloneNode(true);
            clone.classList.add('cloned');
            newPage.appendChild(clone);
        }
        if (child.tagName.toLowerCase() === 'footer') {
            newPage.appendChild(child.cloneNode(true));
            child.classList.add('cloned');
        }
    }

    // collect children
    let children = [firstChildOnNewPage];
    while (firstChildOnNewPage.nextElementSibling !== null) {
        firstChildOnNewPage = firstChildOnNewPage.nextElementSibling;
        if (firstChildOnNewPage.tagName.toLowerCase() !== 'footer') {
            children.push(firstChildOnNewPage);
        }
    }

    // move children
    for (let child of children) {
        // child.parentNode.removeChild(child);
        newPage.appendChild(child);
    }

    // make visible
    page.parentNode.insertBefore(newPage, page.nextSibling);
    console.timeEnd('Splitting page ' + page);
    return newPage;
}

function isTooMuch(page, item) {
    let bottom = item.offsetTop + item.offsetHeight;
    if (item.parentElement !== page && getComputedStyle(item.parentElement).position === 'relative') {
        bottom += item.parentElement.offsetTop;
    }
    let padding = page.firstElementChild.offsetTop;
    for (let child of page.children) {
        if (child.offsetTop)
            padding = Math.min(padding, child.offsetTop);
    }
    // console.log('Too much?', bottom, '>', page.clientHeight, '-', 2 * padding);
    return bottom > page.clientHeight - 2 * padding;
}

function cloneEmpty(element) {
    let newElement = document.createElement(element.tagName);
    for (let cls of element.classList) {
        newElement.classList.add(cls);
    }
    return newElement;
}

// === Actually split pages ===

function splitZauberBogen() {
    function splitMultiColumnList(page, block, list) {
        let allowedHeight = page.clientHeight - block.offsetTop - list.offsetTop - page.firstElementChild.offsetTop;
        allowedHeight -= 10; // safety margin

        // create a new, empty block+list before the current one
        let newList = cloneEmpty(list);
        let newBlock = cloneEmpty(block);
        newBlock.appendChild(newList);
        page.insertBefore(newBlock, block);

        // use the current height to estimate how many elements fit into each column
        let remainingColumns = 2;
        let remainingHeight = 0;
        let lastBottom = 0;
        let childrenSafeForFirstBlock = [];
        let childrenTmp = [];
        for (let item of list.children) {
            childrenTmp.push(item);
            if (item.classList.contains('zauber-infos')) {
                let height = item.offsetTop + item.offsetHeight - lastBottom;
                if (height < 0) {  // original column break
                    height = item.offsetHeight * 2;  // 2x is likely an over-estimation
                }
                if (height > remainingHeight && remainingColumns > 0) {
                    remainingHeight = allowedHeight;
                    remainingColumns--;
                }
                if (height > remainingHeight) {
                    break;
                }
                remainingHeight -= height;
                childrenSafeForFirstBlock = childrenSafeForFirstBlock.concat(childrenTmp);
                childrenTmp = [];
                lastBottom = item.offsetTop + item.offsetHeight;
            }
        }
        for (let item of childrenSafeForFirstBlock) {
            newList.appendChild(item);
        }

        // copy into the new list until it's overfull (squeeze part)
        if (list.children.length <= 500) {
            let uncommited = 0;
            let children = [...list.children];
            let additionalParts = 0;
            for (let item of children) {
                newList.appendChild(item);
                uncommited++;

                if (item.classList.contains('zauber-infos')) {
                    if (isTooMuch(page, newList)) {
                        // move the items back that made it overfull
                        for (let i = 0; i < uncommited; i++) {
                            list.insertBefore(newList.lastChild, list.firstChild);
                        }
                        break;
                    } else {
                        // remove from the old list (=> they can stay here)
                        uncommited = 0;
                        additionalParts++;
                    }
                }
            }
        }

        // split page before the original block (or undo)
        if (newList.children.length > 0 && list.children.length > 0) {
            return splitPage(page, block);
        }
        console.warn('could not split list block', list, newList, newList.children.length, list.children.length);
        // undo on failure
        let nextBlock = block.nextElementSibling;
        if (newList.children.length === 0) {
            newBlock.parentNode.removeChild(newBlock);
        }
        if (list.children.length === 0) {
            block.parentNode.removeChild(block);
        }
        // split after the block (fallback)
        if (nextBlock && nextBlock.tagName.toLowerCase() !== 'footer') {
            return splitPage(page, nextBlock);
        }
    }

    function splitPageIfNecessary(page) {
        if (!page) return;
        let count = 0;
        for (let block of page.querySelectorAll(':scope > .block')) {
            if (isTooMuch(page, block)) {
                // apply in-block splitting if possible
                let lst = block.querySelector('.split-liste');
                if (lst) {
                    let newPage = splitMultiColumnList(page, block, lst);
                    if (newPage) return splitPageIfNecessary(newPage);
                }
                // otherwise split before this element
                if (count > 1) {
                    if (block.previousElementSibling && !block.previousElementSibling.classList.contains('block') && block.previousElementSibling.tagName.startsWith('H')) {
                        block = block.previousElementSibling;  // split before associated headers
                    }
                    let newPage = splitPage(page, block);
                    return splitPageIfNecessary(newPage);
                }
            }
            count += 1;
        }
    }

    let page = document.querySelector('.page-zauberbogen');
    if (page) {
        console.time('Magie page layout');
        splitPageIfNecessary(page);
        console.timeEnd('Magie page layout');
    }
}

function addEmptyZauber(list, count) {
    if (!list) return;
    let lastZauber = list.querySelector(':scope .zauber-infos:last-child');
    if (!lastZauber) return;
    let page = list.parentElement.parentElement;
    let pageHeight = page.clientHeight - page.firstElementChild.offsetTop;

    for (let i = 0; i < count; i++) {
        let lastBlock = last(page.querySelectorAll(':scope .block'));
        if (lastBlock.offsetTop + lastBlock.offsetHeight >= pageHeight - 100) break;

        let span = document.createElement('span');
        span.classList.add('zauber-name');
        span.classList.add('zauber-empty');
        list.appendChild(span);
        span = document.createElement('span');
        span.classList.add('zauber-infos');
        span.classList.add('zauber-empty');
        list.appendChild(span);
    }
}

function splitWesenBogen() {
    function splitPageIfNecessary(page) {
        if (!page) return;
        let count = 0;
        for (let wesenElement of page.querySelectorAll(':scope > .wesen')) {
            if (isTooMuch(page, wesenElement)) {
                // split before this element
                if (count > 1) {
                    let newPage = splitPage(page, wesenElement);
                    return splitPageIfNecessary(newPage);
                }
            }
            count += 1;
        }
    }

    let page = document.querySelector('.page-wesen');
    if (page) {
        console.time('Wesen page layout');
        splitPageIfNecessary(page);
        console.timeEnd('Wesen page layout');
    }
}

function applyAbbreviations(container) {
    container.querySelectorAll('[data-toggle="abbrev"]').forEach(el => {
        if (el.offsetHeight > remToPixels(1.5)) {
            if (el.dataset.text) {
                el.innerText = el.dataset.text;
            }
        }
    });
}

function applyAllAbbreviations(container) {
    container.querySelectorAll('[data-toggle="abbrev"]').forEach(el => {
        if (el.dataset.text) {
            el.innerText = el.dataset.text;
        }
    });
}

function createTooltips() {
    const tooltip = document.createElement('div');
    tooltip.classList.add('tooltip');
    document.body.appendChild(tooltip);
    let currentElement = null;

    document.querySelectorAll('[data-title]').forEach(el => {
        let title = el.getAttribute('data-title');
        el.removeAttribute('data-title');
        if (title === '') return;
        el.classList.add('has-tooltip');

        el.addEventListener("mouseenter", function () {
            tooltip.textContent = title;
            const rect = this.getBoundingClientRect();
            tooltip.style.left = rect.left + window.scrollX + "px";
            tooltip.style.top = rect.bottom + window.scrollY + "px";
            tooltip.classList.add('active');
            currentElement = this;
        });

        el.addEventListener("mouseleave", function () {
            if (currentElement === this) {
                tooltip.classList.remove('active');
                currentElement = null;
            }
        });
    });

    document.body.classList.add('tooltips-initialized');
}

function createModals() {
    function openModal(e) {
        let modal = this.parentElement.querySelector(':scope .modal');
        console.log('Modal', modal);
        if (modal) {
            modal.classList.add('modal-open');
        }
        e.preventDefault();
        e.stopPropagation();
        return false;
    }

    function closeModal(e) {
        let x = this;
        while (!x.classList.contains('modal') && x.parentElement) {
            x = x.parentElement;
        }
        x.classList.remove('modal-open');
        e.preventDefault();
        e.stopPropagation();
        return false;
    }

    document.querySelectorAll('[data-toggle="modal-open"]').forEach(el => {
        el.addEventListener('click', openModal);
    });
    document.querySelectorAll('[data-toggle="modal-close"]').forEach(el => {
        el.addEventListener('click', closeModal);
    });
}

function setLightDark(light) {
    document.documentElement.classList.toggle('light', light);
    document.documentElement.classList.toggle('dark', !light);
}

function toggleWide() {
    document.querySelectorAll('.page').forEach(page => page.classList.toggle('wide'));
    document.querySelectorAll('.buttons').forEach(buttons => buttons.classList.toggle('wide'));
}

function onReady() {
    document.querySelector('.btn-light').addEventListener('click', () => setLightDark(true));
    document.querySelector('.btn-dark').addEventListener('click', () => setLightDark(false));
    document.querySelector('.btn-wide').addEventListener('click', () => toggleWide());
    console.time('Kompakt page layout');
    improveKompaktbogenLayout();
    console.timeEnd('Kompakt page layout');
    splitZauberBogen();
    if (!hatVorteil('Viertelzauberer')) {
        addEmptyZauber(last(document.querySelectorAll('.zauber-liste')), 4);
        addEmptyZauber(last(document.querySelectorAll('.ritual-liste')), 3);
    }
    splitWesenBogen();
    createTooltips();
    createModals();
}

document.addEventListener("DOMContentLoaded", onReady);
