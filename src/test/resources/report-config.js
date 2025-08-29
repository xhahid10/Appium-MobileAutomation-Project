// Configuration for ExtentReports links
document.addEventListener('DOMContentLoaded', function() {
    // Add target="_blank" to all report links
    const links = document.getElementsByTagName('a');
    for (let i = 0; i < links.length; i++) {
        links[i].setAttribute('target', '_blank');
    }
    
    // Add click handler for test cases
    const testCases = document.querySelectorAll('.test-list-item');
    testCases.forEach(function(testCase) {
        testCase.addEventListener('click', function(e) {
            if (e.target.tagName === 'A') {
                e.preventDefault();
                window.open(e.target.href, '_blank');
            }
        });
    });
}); 